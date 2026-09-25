package com.example.ui.practice

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.ListeningAttempt
import com.example.data.models.ListeningTest
import com.example.data.models.ReadingAttempt
import com.example.data.models.ReadingTest
import com.example.data.models.SpeakingAttempt
import com.example.data.models.SpeakingTask
import com.example.data.models.UserProfile
import com.example.data.models.WritingAttempt
import com.example.data.models.WritingTask
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.service.SpeakingEvaluationService
import com.example.data.service.WritingEvaluationService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class SpeakingPhase {
    IDLE, PREP_TIMER, RECORDING, COMPLETED
}

data class PracticeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val readingTests: List<ReadingTest> = emptyList(),
    val listeningTests: List<ListeningTest> = emptyList(),
    val writingTasks: List<WritingTask> = emptyList(),
    val selectedTest: ReadingTest? = null,
    val selectedListeningTest: ListeningTest? = null,
    val selectedWritingTask: WritingTask? = null,
    val userProfile: UserProfile = UserProfile(),
    val attempts: List<ReadingAttempt> = emptyList(),
    // Active Reading Test State
    val userAnswers: Map<Int, String> = emptyMap(),
    val timeRemainingSeconds: Int = 3600,
    val isTimerRunning: Boolean = false,
    val isSubmitted: Boolean = false,
    val currentAttempt: ReadingAttempt? = null,
    val activeTab: Int = 0,
    // Active Listening Test State
    val listeningUserAnswers: Map<Int, String> = emptyMap(),
    val listeningTimeRemainingSeconds: Int = 780, // Audio time + transfer time
    val isAudioPlaying: Boolean = false,
    val hasAudioPlayedOnce: Boolean = false,
    val isTransferTimePhase: Boolean = false,
    val audioError: String? = null,
    val isListeningSubmitted: Boolean = false,
    val currentListeningAttempt: ListeningAttempt? = null,
    // Active Writing Task State
    val writingInputMethod: String = "TEXT", // "TEXT" or "PHOTO_SCAN"
    val writingUserText: String = "",
    val writingPhotoUri: Uri? = null,
    val writingTimeRemainingSeconds: Int = 1200,
    val isWritingTimerRunning: Boolean = false,
    val isWritingEvaluating: Boolean = false,
    val isWritingSubmitted: Boolean = false,
    val currentWritingAttempt: WritingAttempt? = null,
    val writingError: String? = null,
    // Active Speaking Task State
    val speakingTasks: List<SpeakingTask> = emptyList(),
    val selectedSpeakingTask: SpeakingTask? = null,
    val speakingPhase: SpeakingPhase = SpeakingPhase.IDLE,
    val speakingQuestionIndex: Int = 0,
    val speakingPrepTimeRemainingSeconds: Int = 60,
    val speakingRecordingTimeRemainingSeconds: Int = 120,
    val isSpeakingRecording: Boolean = false,
    val speakingAudioFile: File? = null,
    val isSpeakingEvaluating: Boolean = false,
    val isSpeakingSubmitted: Boolean = false,
    val currentSpeakingAttempt: SpeakingAttempt? = null,
    val speakingError: String? = null
)

class PracticeViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val writingEvaluationService = WritingEvaluationService()
    private val speakingEvaluationService = SpeakingEvaluationService()
    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var listeningTimerJob: Job? = null
    private var writingTimerJob: Job? = null
    private var speakingTimerJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val profile = userPreferencesRepository.userProfileFlow.first()
                val readingTests = firestoreRepository.fetchReadingTests()
                val listeningTests = firestoreRepository.fetchListeningTests()
                val writingTasks = firestoreRepository.fetchWritingTasks()
                val speakingTasks = firestoreRepository.fetchSpeakingTasks()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userProfile = profile,
                    readingTests = readingTests,
                    listeningTests = listeningTests,
                    writingTasks = writingTasks,
                    speakingTasks = speakingTasks
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load practice tests. ${e.localizedMessage}"
                )
            }
        }
    }


    fun startReadingTest(testId: String) {
        val test = _uiState.value.readingTests.find { it.id == testId }
            ?: _uiState.value.readingTests.firstOrNull()

        if (test == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Test not found")
            return
        }

        val timeSeconds = test.timeLimitMinutes * 60

        _uiState.value = _uiState.value.copy(
            selectedTest = test,
            userAnswers = emptyMap(),
            timeRemainingSeconds = timeSeconds,
            isTimerRunning = true,
            isSubmitted = false,
            currentAttempt = null,
            activeTab = 0
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0 && _uiState.value.isTimerRunning && !_uiState.value.isSubmitted) {
                delay(1000L)
                val remaining = _uiState.value.timeRemainingSeconds - 1
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = remaining)

                if (remaining <= 0) {
                    submitTest()
                }
            }
        }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun onAnswerSelected(questionId: Int, answer: String) {
        val updatedMap = _uiState.value.userAnswers.toMutableMap()
        updatedMap[questionId] = answer.trim()
        _uiState.value = _uiState.value.copy(userAnswers = updatedMap)
    }

    fun submitTest() {
        timerJob?.cancel()
        val state = _uiState.value
        val test = state.selectedTest ?: return

        var score = 0
        val totalQuestions = test.questions.size

        test.questions.forEach { question ->
            val userAnswer = state.userAnswers[question.id] ?: ""
            if (userAnswer.equals(question.correctAnswer.trim(), ignoreCase = true)) {
                score++
            }
        }

        // IELTS Reading Band Calculation (Approximate based on percentage)
        val percentage = if (totalQuestions > 0) (score.toDouble() / totalQuestions) * 100.0 else 0.0
        val bandScore = when {
            percentage >= 90.0 -> 8.5
            percentage >= 80.0 -> 7.5
            percentage >= 65.0 -> 6.5
            percentage >= 50.0 -> 5.5
            percentage >= 35.0 -> 4.5
            else -> 3.5
        }

        val timeSpent = (test.timeLimitMinutes * 60) - state.timeRemainingSeconds

        val stringAnswers = state.userAnswers.mapKeys { it.key.toString() }

        val attempt = ReadingAttempt(
            id = UUID.randomUUID().toString(),
            testId = test.id,
            testTitle = test.title,
            userId = state.userProfile.phone.ifEmpty { "default_user" },
            userAnswers = stringAnswers,
            score = score,
            totalQuestions = totalQuestions,
            bandScore = bandScore,
            timeTakenSeconds = timeSpent.toLong(),
            timestamp = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            isSubmitted = true,
            isTimerRunning = false,
            currentAttempt = attempt
        )

        viewModelScope.launch {
            firestoreRepository.saveReadingAttempt(attempt)
        }
    }

    fun startListeningTest(testId: String? = null) {

        val tests = _uiState.value.listeningTests
        val userBand = _uiState.value.userProfile.listeningBand
        val targetDiff = when {
            userBand < 6.0 -> "Easy"
            userBand >= 7.5 -> "Hard"
            else -> "Medium"
        }

        val test = if (!testId.isNullOrEmpty()) {
            tests.find { it.id == testId } ?: tests.firstOrNull()
        } else {
            tests.find { it.difficultyLevel.equals(targetDiff, ignoreCase = true) } ?: tests.firstOrNull()
        }

        if (test == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No listening tests available")
            return
        }

        val totalDurationSeconds = test.audioDurationSeconds + test.transferTimeSeconds

        _uiState.value = _uiState.value.copy(
            selectedListeningTest = test,
            listeningUserAnswers = emptyMap(),
            listeningTimeRemainingSeconds = totalDurationSeconds,
            isAudioPlaying = false,
            hasAudioPlayedOnce = false,
            isTransferTimePhase = false,
            audioError = null,
            isListeningSubmitted = false,
            currentListeningAttempt = null
        )

        startListeningTimer()
    }

    private fun startListeningTimer() {
        listeningTimerJob?.cancel()
        listeningTimerJob = viewModelScope.launch {
            while (_uiState.value.listeningTimeRemainingSeconds > 0 && !_uiState.value.isListeningSubmitted) {
                delay(1000L)
                val remaining = _uiState.value.listeningTimeRemainingSeconds - 1
                val test = _uiState.value.selectedListeningTest

                val inTransferPhase = if (test != null) {
                    remaining <= test.transferTimeSeconds
                } else false

                _uiState.value = _uiState.value.copy(
                    listeningTimeRemainingSeconds = remaining,
                    isTransferTimePhase = inTransferPhase
                )

                if (remaining <= 0) {
                    submitListeningTest()
                }
            }
        }
    }

    fun onAudioPlaybackStarted() {
        _uiState.value = _uiState.value.copy(
            isAudioPlaying = true,
            hasAudioPlayedOnce = true,
            audioError = null
        )
    }

    fun onAudioPlaybackCompleted() {
        _uiState.value = _uiState.value.copy(
            isAudioPlaying = false,
            isTransferTimePhase = true
        )
    }

    fun onAudioError(error: String) {
        _uiState.value = _uiState.value.copy(
            isAudioPlaying = false,
            audioError = error
        )
    }

    fun onListeningAnswerSelected(questionId: Int, answer: String) {
        val updated = _uiState.value.listeningUserAnswers.toMutableMap()
        updated[questionId] = answer.trim()
        _uiState.value = _uiState.value.copy(listeningUserAnswers = updated)
    }

    fun submitListeningTest() {
        listeningTimerJob?.cancel()
        val state = _uiState.value
        val test = state.selectedListeningTest ?: return

        var score = 0
        val totalQuestions = test.questions.size

        test.questions.forEach { question ->
            val userAnswer = state.listeningUserAnswers[question.id] ?: ""
            if (userAnswer.equals(question.correctAnswer.trim(), ignoreCase = true)) {
                score++
            }
        }

        val percentage = if (totalQuestions > 0) (score.toDouble() / totalQuestions) * 100.0 else 0.0
        val bandScore = when {
            percentage >= 90.0 -> 8.5
            percentage >= 80.0 -> 7.5
            percentage >= 65.0 -> 6.5
            percentage >= 50.0 -> 5.5
            percentage >= 35.0 -> 4.5
            else -> 3.5
        }

        val totalTime = test.audioDurationSeconds + test.transferTimeSeconds
        val timeSpent = (totalTime - state.listeningTimeRemainingSeconds).coerceAtLeast(0)

        val stringAnswers = state.listeningUserAnswers.mapKeys { it.key.toString() }

        val attempt = ListeningAttempt(
            id = UUID.randomUUID().toString(),
            testId = test.id,
            testTitle = test.title,
            userId = state.userProfile.phone.ifEmpty { "default_user" },
            userAnswers = stringAnswers,
            score = score,
            totalQuestions = totalQuestions,
            bandScore = bandScore,
            timeTakenSeconds = timeSpent.toLong(),
            timestamp = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            isListeningSubmitted = true,
            currentListeningAttempt = attempt
        )

        viewModelScope.launch {
            firestoreRepository.saveListeningAttempt(attempt)
        }
    }

    fun startWritingTask(taskId: String? = null) {
        val tasks = _uiState.value.writingTasks
        val userBand = _uiState.value.userProfile.readingBand
        val targetDiff = when {
            userBand < 6.0 -> "Easy"
            userBand >= 7.5 -> "Hard"
            else -> "Medium"
        }

        val task = if (!taskId.isNullOrEmpty()) {
            tasks.find { it.id == taskId } ?: tasks.firstOrNull()
        } else {
            tasks.find { it.difficultyLevel.equals(targetDiff, ignoreCase = true) } ?: tasks.firstOrNull()
        }

        if (task == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No writing tasks available")
            return
        }

        val timeSeconds = task.recommendedTimeMinutes * 60

        _uiState.value = _uiState.value.copy(
            selectedWritingTask = task,
            writingInputMethod = "TEXT",
            writingUserText = "",
            writingPhotoUri = null,
            writingTimeRemainingSeconds = timeSeconds,
            isWritingTimerRunning = true,
            isWritingEvaluating = false,
            isWritingSubmitted = false,
            currentWritingAttempt = null,
            writingError = null
        )

        startWritingTimer()
    }

    private fun startWritingTimer() {
        writingTimerJob?.cancel()
        writingTimerJob = viewModelScope.launch {
            while (_uiState.value.writingTimeRemainingSeconds > 0 && _uiState.value.isWritingTimerRunning && !_uiState.value.isWritingSubmitted) {
                delay(1000L)
                val remaining = _uiState.value.writingTimeRemainingSeconds - 1
                _uiState.value = _uiState.value.copy(writingTimeRemainingSeconds = remaining)
            }
        }
    }

    fun setWritingInputMethod(method: String) {
        _uiState.value = _uiState.value.copy(writingInputMethod = method)
    }

    fun onWritingTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(writingUserText = text)
    }

    fun onWritingPhotoSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            writingPhotoUri = uri,
            writingInputMethod = if (uri != null) "PHOTO_SCAN" else _uiState.value.writingInputMethod
        )
    }

    fun submitWritingTask(context: Context) {
        writingTimerJob?.cancel()
        val state = _uiState.value
        val task = state.selectedWritingTask ?: return

        _uiState.value = _uiState.value.copy(
            isWritingEvaluating = true,
            isWritingTimerRunning = false,
            writingError = null
        )

        val totalTime = task.recommendedTimeMinutes * 60
        val timeSpent = (totalTime - state.writingTimeRemainingSeconds).coerceAtLeast(0).toLong()

        viewModelScope.launch {
            try {
                val attempt = writingEvaluationService.evaluateWritingSubmission(
                    context = context,
                    task = task,
                    inputMethod = state.writingInputMethod,
                    userText = state.writingUserText,
                    photoUri = state.writingPhotoUri,
                    timeTakenSeconds = timeSpent
                )

                firestoreRepository.saveWritingAttempt(attempt)

                _uiState.value = _uiState.value.copy(
                    isWritingEvaluating = false,
                    isWritingSubmitted = true,
                    currentWritingAttempt = attempt
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isWritingEvaluating = false,
                    writingError = "Failed to evaluate writing: ${e.message}"
                )
            }
        }
    }

    fun resetWritingTask() {
        writingTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            selectedWritingTask = null,
            writingUserText = "",
            writingPhotoUri = null,
            isWritingEvaluating = false,
            isWritingSubmitted = false,
            currentWritingAttempt = null
        )
    }

    fun startSpeakingTask(taskId: String? = null) {
        val tasks = _uiState.value.speakingTasks
        val userBand = _uiState.value.userProfile.speakingBand
        val targetDiff = when {
            userBand < 6.0 -> "Easy"
            userBand >= 7.5 -> "Hard"
            else -> "Medium"
        }

        val task = if (!taskId.isNullOrEmpty()) {
            tasks.find { it.id == taskId } ?: tasks.firstOrNull()
        } else {
            tasks.find { it.difficultyLevel.equals(targetDiff, ignoreCase = true) } ?: tasks.firstOrNull()
        }

        if (task == null) {
            _uiState.value = _uiState.value.copy(speakingError = "No speaking tasks available")
            return
        }

        val initialPhase = if (task.partNumber == 2) SpeakingPhase.PREP_TIMER else SpeakingPhase.RECORDING
        val initialRecTime = when (task.partNumber) {
            1 -> 45
            2 -> 120
            3 -> 60
            else -> 60
        }

        _uiState.value = _uiState.value.copy(
            selectedSpeakingTask = task,
            speakingPhase = initialPhase,
            speakingQuestionIndex = 0,
            speakingPrepTimeRemainingSeconds = 60,
            speakingRecordingTimeRemainingSeconds = initialRecTime,
            isSpeakingRecording = (initialPhase == SpeakingPhase.RECORDING),
            speakingAudioFile = null,
            isSpeakingEvaluating = false,
            isSpeakingSubmitted = false,
            currentSpeakingAttempt = null,
            speakingError = null
        )

        if (initialPhase == SpeakingPhase.PREP_TIMER) {
            startSpeakingPrepTimer()
        } else {
            startSpeakingRecordingTimer()
        }
    }

    private fun startSpeakingPrepTimer() {
        speakingTimerJob?.cancel()
        speakingTimerJob = viewModelScope.launch {
            while (_uiState.value.speakingPrepTimeRemainingSeconds > 0 && _uiState.value.speakingPhase == SpeakingPhase.PREP_TIMER) {
                delay(1000L)
                val remaining = _uiState.value.speakingPrepTimeRemainingSeconds - 1
                if (remaining <= 0) {
                    _uiState.value = _uiState.value.copy(
                        speakingPhase = SpeakingPhase.RECORDING,
                        speakingRecordingTimeRemainingSeconds = 120,
                        isSpeakingRecording = true
                    )
                    startSpeakingRecordingTimer()
                    break
                } else {
                    _uiState.value = _uiState.value.copy(speakingPrepTimeRemainingSeconds = remaining)
                }
            }
        }
    }

    private fun startSpeakingRecordingTimer() {
        speakingTimerJob?.cancel()
        speakingTimerJob = viewModelScope.launch {
            while (_uiState.value.speakingRecordingTimeRemainingSeconds > 0 && _uiState.value.speakingPhase == SpeakingPhase.RECORDING) {
                delay(1000L)
                val remaining = _uiState.value.speakingRecordingTimeRemainingSeconds - 1
                if (remaining <= 0) {
                    handleSpeakingTimeExpired()
                    break
                } else {
                    _uiState.value = _uiState.value.copy(speakingRecordingTimeRemainingSeconds = remaining)
                }
            }
        }
    }

    private fun handleSpeakingTimeExpired() {
        val task = _uiState.value.selectedSpeakingTask ?: return
        val totalQuestions = when (task.partNumber) {
            1 -> task.part1Questions.size
            3 -> task.part3Questions.size
            else -> 1
        }

        val nextIndex = _uiState.value.speakingQuestionIndex + 1
        if (nextIndex < totalQuestions) {
            _uiState.value = _uiState.value.copy(
                speakingQuestionIndex = nextIndex,
                speakingRecordingTimeRemainingSeconds = if (task.partNumber == 1) 45 else 60
            )
            startSpeakingRecordingTimer()
        } else {
            _uiState.value = _uiState.value.copy(
                speakingPhase = SpeakingPhase.COMPLETED,
                isSpeakingRecording = false
            )
        }
    }

    fun nextSpeakingQuestion() {
        handleSpeakingTimeExpired()
    }

    fun skipSpeakingPrep() {
        speakingTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            speakingPhase = SpeakingPhase.RECORDING,
            speakingRecordingTimeRemainingSeconds = 120,
            isSpeakingRecording = true
        )
        startSpeakingRecordingTimer()
    }

    fun submitSpeakingTask(context: Context, recordedFile: File?, simulatedTranscript: String? = null) {
        speakingTimerJob?.cancel()
        val task = _uiState.value.selectedSpeakingTask ?: return

        _uiState.value = _uiState.value.copy(
            isSpeakingEvaluating = true,
            isSpeakingRecording = false,
            speakingPhase = SpeakingPhase.COMPLETED,
            speakingError = null
        )

        viewModelScope.launch {
            try {
                val attempt = speakingEvaluationService.evaluateSpeakingSubmission(
                    context = context,
                    task = task,
                    audioFile = recordedFile,
                    simulatedTranscript = simulatedTranscript
                )

                firestoreRepository.saveSpeakingAttempt(attempt)

                _uiState.value = _uiState.value.copy(
                    isSpeakingEvaluating = false,
                    isSpeakingSubmitted = true,
                    currentSpeakingAttempt = attempt
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSpeakingEvaluating = false,
                    speakingError = "Failed to evaluate speaking response: ${e.message}"
                )
            }
        }
    }

    fun resetSpeakingTask() {
        speakingTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            selectedSpeakingTask = null,
            speakingPhase = SpeakingPhase.IDLE,
            speakingQuestionIndex = 0,
            isSpeakingEvaluating = false,
            isSpeakingSubmitted = false,
            currentSpeakingAttempt = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        listeningTimerJob?.cancel()
        writingTimerJob?.cancel()
        speakingTimerJob?.cancel()
    }
}

