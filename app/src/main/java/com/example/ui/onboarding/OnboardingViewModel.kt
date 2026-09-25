package com.example.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.UserProfile
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0, // 0: Target Band, 1: Test Date, 2: Skill Sliders
    val targetBand: Double = 7.0,
    val testDateOption: String = "In 3 Months",
    val listeningBand: Double = 6.0,
    val readingBand: Double = 6.0,
    val writingBand: Double = 5.5,
    val speakingBand: Double = 5.5,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userProfileFlow.collect { profile ->
                _uiState.value = _uiState.value.copy(
                    targetBand = profile.targetBand,
                    testDateOption = profile.testDate,
                    listeningBand = profile.listeningBand,
                    readingBand = profile.readingBand,
                    writingBand = profile.writingBand,
                    speakingBand = profile.speakingBand
                )
            }
        }
    }

    fun setTargetBand(band: Double) {
        _uiState.value = _uiState.value.copy(targetBand = band)
    }

    fun setTestDate(dateOption: String) {
        _uiState.value = _uiState.value.copy(testDateOption = dateOption)
    }

    fun updateListeningBand(band: Double) {
        _uiState.value = _uiState.value.copy(listeningBand = (band * 2).toInt() / 2.0)
    }

    fun updateReadingBand(band: Double) {
        _uiState.value = _uiState.value.copy(readingBand = (band * 2).toInt() / 2.0)
    }

    fun updateWritingBand(band: Double) {
        _uiState.value = _uiState.value.copy(writingBand = (band * 2).toInt() / 2.0)
    }

    fun updateSpeakingBand(band: Double) {
        _uiState.value = _uiState.value.copy(speakingBand = (band * 2).toInt() / 2.0)
    }

    fun nextStep() {
        if (_uiState.value.currentStep < 2) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        } else {
            completeOnboarding()
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    private fun completeOnboarding() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val state = _uiState.value
            val currentProfile = userPreferencesRepository.userProfileFlow.first()
            val profile = currentProfile.copy(
                targetBand = state.targetBand,
                testDate = state.testDateOption,
                listeningBand = state.listeningBand,
                readingBand = state.readingBand,
                writingBand = state.writingBand,
                speakingBand = state.speakingBand,
                isOnboardingCompleted = true
            )
            userPreferencesRepository.saveUserProfile(profile)
            try {
                firestoreRepository.saveUserProfile(profile)
            } catch (e: Exception) {
                // Ignore background sync errors
            }
            _uiState.value = _uiState.value.copy(isLoading = false, isCompleted = true)
        }
    }
}
