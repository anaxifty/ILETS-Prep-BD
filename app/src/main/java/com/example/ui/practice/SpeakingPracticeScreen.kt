package com.example.ui.practice

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.models.SpeakingTask
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingPracticeScreen(
    uiState: PracticeUiState,
    onSkipPrep: () -> Unit,
    onNextQuestion: () -> Unit,
    onSubmitSpeaking: (File?, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val task = uiState.selectedSpeakingTask ?: return
    val scrollState = rememberScrollState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // MediaRecorder local management
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var currentAudioFile by remember { mutableStateOf<File?>(null) }
    var isRecordingActive by remember { mutableStateOf(false) }

    // Start / Stop recorder effect based on state
    DisposableEffect(uiState.isSpeakingRecording) {
        if (uiState.isSpeakingRecording && hasMicPermission && !isRecordingActive) {
            try {
                val file = File(context.cacheDir, "speaking_rec_${System.currentTimeMillis()}.3gp")
                val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                recorder.setOutputFile(file.absolutePath)
                recorder.prepare()
                recorder.start()

                mediaRecorder = recorder
                currentAudioFile = file
                isRecordingActive = true
            } catch (e: Exception) {
                Log.e("SpeakingPractice", "MediaRecorder failed to start: ${e.message}")
            }
        }

        onDispose {
            if (isRecordingActive) {
                try {
                    mediaRecorder?.stop()
                    mediaRecorder?.release()
                } catch (e: Exception) {
                    Log.e("SpeakingPractice", "Error stopping MediaRecorder: ${e.message}")
                }
                mediaRecorder = null
                isRecordingActive = false
            }
        }
    }

    // Pulse animation for live recording indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        bottomBar = {
            LumenSurface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = LumenTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.speakingPhase == SpeakingPhase.PREP_TIMER) {
                        LumenOutlinedButton(
                            onClick = onSkipPrep,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("skip_prep_button")
                        ) {
                            Text("Skip Prep & Start Speaking")
                        }
                    } else {
                        val totalQuestions = when (task.partNumber) {
                            1 -> task.part1Questions.size
                            3 -> task.part3Questions.size
                            else -> 1
                        }
                        val isLastQuestion = (uiState.speakingQuestionIndex >= totalQuestions - 1)

                        if (!isLastQuestion) {
                            LumenButton(
                                onClick = onNextQuestion,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("next_speaking_q_button")
                            ) {
                                Text("Next Question")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            LumenButton(
                                onClick = {
                                    // Stop recorder if running
                                    if (isRecordingActive) {
                                        try {
                                            mediaRecorder?.stop()
                                            mediaRecorder?.release()
                                        } catch (e: Exception) {
                                            Log.e("SpeakingPractice", "Error stopping recorder on submit: ${e.message}")
                                        }
                                        mediaRecorder = null
                                        isRecordingActive = false
                                    }
                                    onSubmitSpeaking(currentAudioFile, null)
                                },
                                enabled = !uiState.isSpeakingEvaluating,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LumenTheme.colors.primary,
                                    contentColor = LumenTheme.colors.onPrimary
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("submit_speaking_button")
                            ) {
                                Text(
    "Complete & Evaluate",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LumenIconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.surfaceVariant)
                                .testTag("speaking_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = LumenTheme.colors.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LumenTheme.colors.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
    text = "PART ${task.partNumber}",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            }
                            Text(
    text = task.title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                        }
                    }

                    // Recording / Prep Pill Timer
                    val isPrep = uiState.speakingPhase == SpeakingPhase.PREP_TIMER
                    val secondsLeft = if (isPrep) uiState.speakingPrepTimeRemainingSeconds else uiState.speakingRecordingTimeRemainingSeconds
                    val mins = secondsLeft / 60
                    val secs = secondsLeft % 60
                    val timeStr = "%02d:%02d".format(mins, secs)

                    LumenSurface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isPrep) LumenTheme.colors.tertiaryContainer else LumenTheme.colors.errorContainer,
                        modifier = Modifier.testTag("speaking_timer_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPrep) Icons.Default.Timer else Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isPrep) LumenTheme.colors.onTertiaryContainer else LumenTheme.colors.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
    text = if (isPrep) "Prep $timeStr" else "Rec $timeStr",
    style = (LumenTheme.typography.labelLarge).copy(fontWeight = FontWeight.Bold),
    color = if (isPrep) LumenTheme.colors.onTertiaryContainer else LumenTheme.colors.onErrorContainer
)
                        }
                    }
                }

                // Mic Permission Warning Banner if missing
                if (!hasMicPermission) {
                    LumenCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.errorContainer),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MicOff,
                                contentDescription = null,
                                tint = LumenTheme.colors.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
    text = "Microphone Permission Required",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onErrorContainer
)
                                Text(
                                    text = "Grant mic access to record your voice. In emulator mode, Gemini fallback simulates real responses.",
                                    style = LumenTheme.typography.bodySmall,
                                    color = LumenTheme.colors.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            LumenButton(
                                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Grant")
                            }
                        }
                    }
                }

                // Main Prompt LumenCard
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
    text = "EXAMINER QUESTION PROMPT",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                            if (task.partNumber == 1 || task.partNumber == 3) {
                                val totalQ = if (task.partNumber == 1) task.part1Questions.size else task.part3Questions.size
                                LumenSurface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = LumenTheme.colors.surface
                                ) {
                                    Text(
    text = "Q ${uiState.speakingQuestionIndex + 1} of $totalQ",
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold)
)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (task.partNumber) {
                            1 -> {
                                val currentQ = task.part1Questions.getOrNull(uiState.speakingQuestionIndex)
                                    ?: "Tell me about your daily routine."
                                Text(
    text = "\"$currentQ\"",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold, lineHeight = 30.sp),
    color = LumenTheme.colors.onSurfaceVariant
)
                            }
                            2 -> {
                                Text(
    text = task.part2CueCard,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)

                                if (task.part2Bullets.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
    text = "You should say:",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.primary
)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    task.part2Bullets.forEach { bullet ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text(
    "• ",
    color = LumenTheme.colors.primary,
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                                            Text(text = bullet, style = LumenTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                            3 -> {
                                val currentQ = task.part3Questions.getOrNull(uiState.speakingQuestionIndex)
                                    ?: "How do you think this issue will evolve in the future?"
                                Text(
    text = "\"$currentQ\"",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold, lineHeight = 30.sp),
    color = LumenTheme.colors.onSurfaceVariant
)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Live Audio Waveform / Status Visualizer
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.speakingPhase == SpeakingPhase.PREP_TIMER) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = LumenTheme.colors.tertiary,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
    text = "1-Minute Preparation Time",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Organize your thoughts and key bullet points before speaking.",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        } else {
                            // Recording pulse animation
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(LumenTheme.colors.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(LumenTheme.colors.error),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Recording",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
    text = "Recording Active — Speak Clearly",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.error
)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Maintain a steady speaking pace and use formal IELTS vocabulary.",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Evaluation Progress Overlay
            if (uiState.isSpeakingEvaluating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    LumenCard(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LumenSpinner(
                                modifier = Modifier.size(56.dp),
                                color = LumenTheme.colors.primary,
                                strokeWidth = 4.dp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
    text = "Analyzing Spoken Audio...",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Senior Examiner is assessing Fluency, Vocabulary, Grammar, and Multimodal Pronunciation...",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
