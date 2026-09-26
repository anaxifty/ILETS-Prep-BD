package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.OtpVerificationScreen
import com.example.ui.auth.PhoneAuthScreen
import com.example.ui.home.HomeScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingViewModel
import com.example.ui.practice.ListeningPracticeScreen
import com.example.ui.practice.ListeningResultsScreen
import com.example.ui.practice.PracticeHubScreen
import com.example.ui.practice.PracticeViewModel
import com.example.ui.practice.ReadingPracticeScreen
import com.example.ui.practice.ReadingResultsScreen
import com.example.ui.practice.SpeakingPracticeScreen
import com.example.ui.practice.SpeakingResultsScreen
import com.example.ui.practice.WritingPracticeScreen
import com.example.ui.practice.WritingResultsScreen
import com.example.ui.lessons.VideoLessonPlayerScreen
import com.example.ui.lessons.VideoLessonsListScreen
import com.example.ui.lessons.VideoLessonsViewModel
import com.example.ui.mockexam.MockExamFlowScreen
import com.example.ui.mockexam.MockExamListScreen
import com.example.ui.mockexam.MockExamResultsScreen
import com.example.ui.mockexam.MockExamViewModel
import com.example.ui.center.CenterLocatorScreen
import com.example.ui.center.CenterDetailScreen
import com.example.ui.center.MyCenterBookingsScreen
import com.example.ui.center.CenterLocatorViewModel

object NavRoutes {
    const val PHONE_AUTH = "phone_auth"
    const val OTP_VERIFY = "otp_verify"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val PRACTICE_HUB = "practice_hub"
    const val READING_PRACTICE = "reading_practice"
    const val READING_RESULTS = "reading_results"
    const val LISTENING_PRACTICE = "listening_practice"
    const val LISTENING_RESULTS = "listening_results"
    const val WRITING_PRACTICE = "writing_practice"
    const val WRITING_RESULTS = "writing_results"
    const val SPEAKING_PRACTICE = "speaking_practice"
    const val SPEAKING_RESULTS = "speaking_results"
    const val VIDEO_LESSONS_LIST = "video_lessons_list"
    const val VIDEO_LESSON_PLAYER = "video_lesson_player"
    const val MOCK_EXAM_LIST = "mock_exam_list"
    const val MOCK_EXAM_FLOW = "mock_exam_flow"
    const val MOCK_EXAM_RESULTS = "mock_exam_results"
    const val CENTER_LOCATOR = "center_locator"
    const val CENTER_DETAIL = "center_detail"
    const val MY_CENTER_BOOKINGS = "my_center_bookings"
}


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    val authRepository = remember { AuthRepository() }
    val firestoreRepository = remember { FirestoreRepository() }

    val userProfileState by userPreferencesRepository.userProfileFlow.collectAsState(initial = null)

    if (userProfileState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LumenSpinner()
        }
        return
    }

    val initialRoute = remember {
        when {
            userProfileState?.isOnboardingCompleted == true -> NavRoutes.HOME
            authRepository.isUserSignedIn || userProfileState?.phone?.isNotEmpty() == true -> NavRoutes.ONBOARDING
            else -> NavRoutes.PHONE_AUTH
        }
    }

    // Shared PracticeViewModel for the practice sub-flow
    val practiceViewModel: PracticeViewModel = viewModel(
        factory = SimpleViewModelFactory { PracticeViewModel(firestoreRepository, userPreferencesRepository) }
    )
    val practiceUiState by practiceViewModel.uiState.collectAsState()

    // Shared VideoLessonsViewModel for video learning library
    val videoLessonsViewModel: VideoLessonsViewModel = viewModel(
        factory = SimpleViewModelFactory { VideoLessonsViewModel(firestoreRepository, userPreferencesRepository) }
    )
    val videoLessonsUiState by videoLessonsViewModel.uiState.collectAsState()

    // Shared MockExamViewModel for Weekly & Monthly Mock Exams
    val mockExamViewModel: MockExamViewModel = viewModel(
        factory = SimpleViewModelFactory { MockExamViewModel(firestoreRepository, userPreferencesRepository) }
    )
    val mockExamUiState by mockExamViewModel.uiState.collectAsState()

    // Shared CenterLocatorViewModel for partner coaching center locator and slot bookings
    val centerLocatorViewModel: CenterLocatorViewModel = viewModel(
        factory = SimpleViewModelFactory { CenterLocatorViewModel(firestoreRepository, userPreferencesRepository) }
    )
    val centerLocatorUiState by centerLocatorViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = initialRoute,
        modifier = modifier
    ) {
        composable(NavRoutes.PHONE_AUTH) {
            val viewModel: AuthViewModel = viewModel(
                factory = SimpleViewModelFactory { AuthViewModel(authRepository, userPreferencesRepository) }
            )
            val uiState by viewModel.uiState.collectAsState()

            androidx.compose.runtime.LaunchedEffect(uiState.isOtpSent, uiState.isAuthenticated) {
                if (uiState.isOtpSent) {
                    navController.navigate(NavRoutes.OTP_VERIFY)
                } else if (uiState.isAuthenticated) {
                    navController.navigate(NavRoutes.ONBOARDING) {
                        popUpTo(NavRoutes.PHONE_AUTH) { inclusive = true }
                    }
                }
            }

            PhoneAuthScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToOtp = { navController.navigate(NavRoutes.OTP_VERIFY) }
            )
        }

        composable(NavRoutes.OTP_VERIFY) {
            val viewModel: AuthViewModel = viewModel(
                factory = SimpleViewModelFactory { AuthViewModel(authRepository, userPreferencesRepository) }
            )
            val uiState by viewModel.uiState.collectAsState()

            androidx.compose.runtime.LaunchedEffect(uiState.isAuthenticated) {
                if (uiState.isAuthenticated) {
                    navController.navigate(NavRoutes.ONBOARDING) {
                        popUpTo(NavRoutes.PHONE_AUTH) { inclusive = true }
                    }
                }
            }

            OtpVerificationScreen(
                viewModel = viewModel,
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(NavRoutes.ONBOARDING) {
                        popUpTo(NavRoutes.PHONE_AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(
                factory = SimpleViewModelFactory { OnboardingViewModel(userPreferencesRepository) }
            )
            val uiState by viewModel.uiState.collectAsState()

            OnboardingScreen(
                viewModel = viewModel,
                uiState = uiState,
                onOnboardingFinished = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                userPreferencesRepository = userPreferencesRepository,
                onSignOut = {
                    authRepository.signOut()
                    navController.navigate(NavRoutes.PHONE_AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToPractice = {
                    practiceViewModel.loadData()
                    navController.navigate(NavRoutes.PRACTICE_HUB)
                },
                onNavigateToVideoLessons = {
                    videoLessonsViewModel.loadData(context)
                    navController.navigate(NavRoutes.VIDEO_LESSONS_LIST)
                },
                onNavigateToMockExams = {
                    mockExamViewModel.loadMockExamsData()
                    navController.navigate(NavRoutes.MOCK_EXAM_LIST)
                },
                onNavigateToCenterLocator = {
                    centerLocatorViewModel.loadData()
                    navController.navigate(NavRoutes.CENTER_LOCATOR)
                }
            )
        }

        composable(NavRoutes.PRACTICE_HUB) {
            PracticeHubScreen(
                uiState = practiceUiState,
                onStartReadingTest = { testId ->
                    practiceViewModel.startReadingTest(testId)
                    navController.navigate(NavRoutes.READING_PRACTICE)
                },
                onStartListeningTest = { testId ->
                    practiceViewModel.startListeningTest(testId)
                    navController.navigate(NavRoutes.LISTENING_PRACTICE)
                },
                onStartWritingTask = { taskId ->
                    practiceViewModel.startWritingTask(taskId)
                    navController.navigate(NavRoutes.WRITING_PRACTICE)
                },
                onStartSpeakingTask = { taskId ->
                    practiceViewModel.startSpeakingTask(taskId)
                    navController.navigate(NavRoutes.SPEAKING_PRACTICE)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.READING_PRACTICE) {
            ReadingPracticeScreen(
                viewModel = practiceViewModel,
                uiState = practiceUiState,
                onBack = { navController.popBackStack() },
                onSubmitted = {
                    navController.navigate(NavRoutes.READING_RESULTS) {
                        popUpTo(NavRoutes.READING_PRACTICE) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.READING_RESULTS) {
            ReadingResultsScreen(
                uiState = practiceUiState,
                onDone = {
                    navController.navigate(NavRoutes.PRACTICE_HUB) {
                        popUpTo(NavRoutes.PRACTICE_HUB) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.LISTENING_PRACTICE) {
            ListeningPracticeScreen(
                uiState = practiceUiState,
                onAnswerSelected = { questionId, answer ->
                    practiceViewModel.onListeningAnswerSelected(questionId, answer)
                },
                onAudioPlaybackStarted = {
                    practiceViewModel.onAudioPlaybackStarted()
                },
                onAudioPlaybackCompleted = {
                    practiceViewModel.onAudioPlaybackCompleted()
                },
                onAudioError = { errorMsg ->
                    practiceViewModel.onAudioError(errorMsg)
                },
                onSubmit = {
                    practiceViewModel.submitListeningTest()
                    navController.navigate(NavRoutes.LISTENING_RESULTS) {
                        popUpTo(NavRoutes.LISTENING_PRACTICE) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.LISTENING_RESULTS) {
            ListeningResultsScreen(
                uiState = practiceUiState,
                onBackToHub = {
                    navController.navigate(NavRoutes.PRACTICE_HUB) {
                        popUpTo(NavRoutes.PRACTICE_HUB) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.WRITING_PRACTICE) {
            androidx.compose.runtime.LaunchedEffect(practiceUiState.isWritingSubmitted) {
                if (practiceUiState.isWritingSubmitted) {
                    navController.navigate(NavRoutes.WRITING_RESULTS) {
                        popUpTo(NavRoutes.WRITING_PRACTICE) { inclusive = true }
                    }
                }
            }

            WritingPracticeScreen(
                uiState = practiceUiState,
                onTextChanged = { text ->
                    practiceViewModel.onWritingTextChanged(text)
                },
                onPhotoSelected = { uri ->
                    practiceViewModel.onWritingPhotoSelected(uri)
                },
                onInputMethodChanged = { method ->
                    practiceViewModel.setWritingInputMethod(method)
                },
                onSubmit = {
                    practiceViewModel.submitWritingTask(context)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.WRITING_RESULTS) {
            WritingResultsScreen(
                attempt = practiceUiState.currentWritingAttempt,
                onReturnToHub = {
                    practiceViewModel.resetWritingTask()
                    navController.navigate(NavRoutes.PRACTICE_HUB) {
                        popUpTo(NavRoutes.PRACTICE_HUB) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.SPEAKING_PRACTICE) {
            androidx.compose.runtime.LaunchedEffect(practiceUiState.isSpeakingSubmitted) {
                if (practiceUiState.isSpeakingSubmitted) {
                    navController.navigate(NavRoutes.SPEAKING_RESULTS) {
                        popUpTo(NavRoutes.SPEAKING_PRACTICE) { inclusive = true }
                    }
                }
            }

            SpeakingPracticeScreen(
                uiState = practiceUiState,
                onSkipPrep = {
                    practiceViewModel.skipSpeakingPrep()
                },
                onNextQuestion = {
                    practiceViewModel.nextSpeakingQuestion()
                },
                onSubmitSpeaking = { audioFile, simulatedTranscript ->
                    practiceViewModel.submitSpeakingTask(context, audioFile, simulatedTranscript)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SPEAKING_RESULTS) {
            SpeakingResultsScreen(
                attempt = practiceUiState.currentSpeakingAttempt,
                onReturnToHub = {
                    practiceViewModel.resetSpeakingTask()
                    navController.navigate(NavRoutes.PRACTICE_HUB) {
                        popUpTo(NavRoutes.PRACTICE_HUB) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.VIDEO_LESSONS_LIST) {
            VideoLessonsListScreen(
                viewModel = videoLessonsViewModel,
                uiState = videoLessonsUiState,
                onSelectLesson = { lesson ->
                    navController.navigate(NavRoutes.VIDEO_LESSON_PLAYER)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VIDEO_LESSON_PLAYER) {
            VideoLessonPlayerScreen(
                lesson = videoLessonsUiState.currentPlayingLesson,
                viewModel = videoLessonsViewModel,
                uiState = videoLessonsUiState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MOCK_EXAM_LIST) {
            MockExamListScreen(
                viewModel = mockExamViewModel,
                uiState = mockExamUiState,
                onStartExam = { exam ->
                    navController.navigate(NavRoutes.MOCK_EXAM_FLOW)
                },
                onViewResult = { attempt ->
                    navController.navigate(NavRoutes.MOCK_EXAM_RESULTS)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MOCK_EXAM_FLOW) {
            MockExamFlowScreen(
                viewModel = mockExamViewModel,
                uiState = mockExamUiState,
                onExamCompleted = {
                    navController.navigate(NavRoutes.MOCK_EXAM_RESULTS) {
                        popUpTo(NavRoutes.MOCK_EXAM_LIST)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MOCK_EXAM_RESULTS) {
            val attempt = mockExamUiState.lastCompletedAttempt
                ?: mockExamUiState.userAttempts.firstOrNull { it.status == "COMPLETED" }
                ?: com.example.data.models.MockExamAttempt()

            MockExamResultsScreen(
                attempt = attempt,
                pastAttempts = mockExamUiState.userAttempts.filter { it.status == "COMPLETED" },
                onDone = {
                    navController.navigate(NavRoutes.MOCK_EXAM_LIST) {
                        popUpTo(NavRoutes.MOCK_EXAM_LIST) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.CENTER_LOCATOR) {
            CenterLocatorScreen(
                viewModel = centerLocatorViewModel,
                uiState = centerLocatorUiState,
                onSelectCenter = { center ->
                    centerLocatorViewModel.selectCenter(center)
                    navController.navigate(NavRoutes.CENTER_DETAIL)
                },
                onNavigateToMyBookings = {
                    navController.navigate(NavRoutes.MY_CENTER_BOOKINGS)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CENTER_DETAIL) {
            val selectedCenter = centerLocatorUiState.selectedCenter
                ?: centerLocatorUiState.allCenters.firstOrNull()
                ?: com.example.data.models.PartnerCenter()

            CenterDetailScreen(
                center = selectedCenter,
                onBookSlot = { slot, studentName, studentPhone ->
                    centerLocatorViewModel.initiateSSLCommerzPayment(
                        center = selectedCenter,
                        slot = slot,
                        studentName = studentName,
                        studentPhone = studentPhone
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MY_CENTER_BOOKINGS) {
            MyCenterBookingsScreen(
                bookings = centerLocatorUiState.userBookings,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


// Simple ViewModel Factory helper
class SimpleViewModelFactory<T : androidx.lifecycle.ViewModel>(
    private val creator: () -> T
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
