package com.example.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.PhoneAuthState
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AuthMode {
    PHONE,
    EMAIL
}

data class AuthUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val email: String = "",
    val password: String = "",
    val authMode: AuthMode = AuthMode.PHONE,
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setAuthMode(mode: AuthMode) {
        _uiState.value = _uiState.value.copy(
            authMode = mode,
            errorMessage = null
        )
    }

    fun onPhoneNumberChanged(number: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = number.filter { it.isDigit() || it == '+' },
            errorMessage = null
        )
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun onOtpCodeChanged(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                otpCode = code,
                errorMessage = null
            )
        }
    }

    fun sendOtp(activity: Activity) {
        val rawNumber = _uiState.value.phoneNumber.trim()
        val formattedNumber = formatBangladeshiPhone(rawNumber)

        if (formattedNumber == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid Bangladeshi phone number (e.g., 01712345678)"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.sendOtpCode(activity, formattedNumber).collect { state ->
                when (state) {
                    is PhoneAuthState.CodeSent -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isOtpSent = true,
                            phoneNumber = formattedNumber,
                            errorMessage = null
                        )
                    }
                    is PhoneAuthState.AutoVerified -> {
                        val currentProfile = userPreferencesRepository.userProfileFlow.first()
                        userPreferencesRepository.saveUserProfile(
                            currentProfile.copy(phone = formattedNumber)
                        )
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true
                        )
                    }
                    is PhoneAuthState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = state.message
                        )
                    }
                    PhoneAuthState.Idle -> {}
                }
            }
        }
    }

    fun verifyOtp() {
        val otp = _uiState.value.otpCode.trim()
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter the complete 6-digit OTP code"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        authRepository.verifyOtpCode(otp) { success, errorMsg ->
            viewModelScope.launch {
                if (success) {
                    val currentPhone = _uiState.value.phoneNumber.ifEmpty { "+8801700000000" }
                    val currentProfile = userPreferencesRepository.userProfileFlow.first()
                    userPreferencesRepository.saveUserProfile(
                        currentProfile.copy(phone = currentPhone)
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg ?: "Verification failed"
                    )
                }
            }
        }
    }

    fun signInWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid email address (e.g. user@example.com)"
            )
            return
        }

        if (password.length < 4) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password must be at least 4 characters long"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val currentProfile = userPreferencesRepository.userProfileFlow.first()
            userPreferencesRepository.saveUserProfile(
                currentProfile.copy(phone = email)
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true
            )
        }
    }

    fun signInWithGoogle() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val currentProfile = userPreferencesRepository.userProfileFlow.first()
            userPreferencesRepository.saveUserProfile(
                currentProfile.copy(phone = "google_user@gmail.com")
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true
            )
        }
    }

    fun signInAsGuest() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val currentProfile = userPreferencesRepository.userProfileFlow.first()
            userPreferencesRepository.saveUserProfile(
                currentProfile.copy(phone = "+8801700000000")
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    private fun formatBangladeshiPhone(input: String): String? {
        val clean = input.replace(" ", "").replace("-", "")
        return when {
            clean.startsWith("+880") && clean.length == 14 -> clean
            clean.startsWith("880") && clean.length == 13 -> "+$clean"
            clean.startsWith("01") && clean.length == 11 -> "+88$clean"
            clean.length == 10 && clean.startsWith("1") -> "+880$clean"
            else -> null
        }
    }
}

