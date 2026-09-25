package com.example.ui.mockexam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MockExamUiState(
    val mockExams: List<MockExam> = emptyList(),
    val userAttempts: List<MockExamAttempt> = emptyList(),
    val activeAttempt: MockExamAttempt? = null,
    val selectedMockExam: MockExam? = null,
    val currentListeningTest: ListeningTest? = null,
    val currentReadingTest: ReadingTest? = null,
    val currentWritingTask: WritingTask? = null,
    val currentSpeakingTask: SpeakingTask? = null,
    val lastCompletedAttempt: MockExamAttempt? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MockExamViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MockExamUiState())
    val uiState: StateFlow<MockExamUiState> = _uiState.asStateFlow()

    fun loadMockExamsData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = userPreferencesRepository.userProfileFlow.first()
                val userId = profile.phone.ifEmpty { "default_user" }

                val exams = firestoreRepository.fetchMockExams()
                val attempts = firestoreRepository.fetchUserMockAttempts(userId)

                val active = attempts.firstOrNull { it.status == "IN_PROGRESS" }

                _uiState.update {
                    it.copy(
                        mockExams = exams,
                        userAttempts = attempts,
                        activeAttempt = active,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load mock exams: ${e.message}"
                    )
                }
            }
        }
    }

    fun startOrResumeMockExam(exam: MockExam) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = userPreferencesRepository.userProfileFlow.first()
                val userId = profile.phone.ifEmpty { "default_user" }

                // Fetch tests required for this mock exam
                val listeningTests = firestoreRepository.fetchListeningTests()
                val readingTests = firestoreRepository.fetchReadingTests()
                val writingTasks = firestoreRepository.fetchWritingTasks()
                val speakingTasks = firestoreRepository.fetchSpeakingTasks()

                val listeningTest = listeningTests.find { it.id == exam.listeningTestId } ?: listeningTests.firstOrNull()
                val readingTest = readingTests.find { it.id == exam.readingTestId } ?: readingTests.firstOrNull()
                val writingTask = writingTasks.find { it.id == exam.writingTaskId } ?: writingTasks.firstOrNull()
                val speakingTask = speakingTasks.find { it.id == exam.speakingTaskId } ?: speakingTasks.firstOrNull()

                // Check if user has an active attempt for this exam
                var attempt = _uiState.value.userAttempts.firstOrNull {
                    it.mockExamId == exam.id && it.status == "IN_PROGRESS"
                }

                if (attempt == null) {
                    // Create new active attempt
                    attempt = MockExamAttempt(
                        attemptId = "mock_att_" + UUID.randomUUID().toString().take(8),
                        mockExamId = exam.id,
                        userId = userId,
                        examTitle = exam.title,
                        examType = exam.examType,
                        startTimeMillis = System.currentTimeMillis(),
                        status = "IN_PROGRESS",
                        currentSection = "LISTENING"
                    )
                    firestoreRepository.saveMockExamAttempt(userId, attempt)
                }

                _uiState.update {
                    it.copy(
                        selectedMockExam = exam,
                        activeAttempt = attempt,
                        currentListeningTest = listeningTest,
                        currentReadingTest = readingTest,
                        currentWritingTask = writingTask,
                        currentSpeakingTask = speakingTask,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Could not initialize mock exam: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateActiveAttemptSection(nextSection: String, updatedAttempt: MockExamAttempt) {
        viewModelScope.launch {
            val profile = userPreferencesRepository.userProfileFlow.first()
            val userId = profile.phone.ifEmpty { "default_user" }

            val newAttempt = updatedAttempt.copy(currentSection = nextSection)
            _uiState.update { it.copy(activeAttempt = newAttempt) }
            firestoreRepository.saveMockExamAttempt(userId, newAttempt)
        }
    }

    fun finalizeMockExam(completedAttempt: MockExamAttempt) {
        viewModelScope.launch {
            val profile = userPreferencesRepository.userProfileFlow.first()
            val userId = profile.phone.ifEmpty { "default_user" }

            // Apply official IELTS rounding rule
            val calculatedOverall = IeltsScoreCalculator.calculateOverallBand(
                listening = completedAttempt.listeningBand,
                reading = completedAttempt.readingBand,
                writing = completedAttempt.writingBand,
                speaking = completedAttempt.speakingBand
            )

            val totalDurationMinutes = _uiState.value.selectedMockExam?.totalDurationMinutes ?: 160
            val officialTimeUp = if (completedAttempt.officialTimeUpTimeMillis > 0L) {
                completedAttempt.officialTimeUpTimeMillis
            } else {
                completedAttempt.startTimeMillis + (totalDurationMinutes * 60 * 1000L)
            }
            val now = System.currentTimeMillis()

            val finalAttempt = completedAttempt.copy(
                status = "COMPLETED",
                currentSection = "COMPLETED",
                endTimeMillis = now,
                officialTimeUpTimeMillis = officialTimeUp,
                actualSubmissionTimeMillis = now,
                overallBand = calculatedOverall
            )

            firestoreRepository.saveMockExamAttempt(userId, finalAttempt)

            // Refresh user attempts list
            val updatedAttempts = firestoreRepository.fetchUserMockAttempts(userId)

            _uiState.update {
                it.copy(
                    activeAttempt = null,
                    lastCompletedAttempt = finalAttempt,
                    userAttempts = updatedAttempts
                )
            }
        }
    }

    fun setSelectedCompletedAttempt(attempt: MockExamAttempt) {
        _uiState.update { it.copy(lastCompletedAttempt = attempt) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
