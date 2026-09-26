package com.example.ui.practice

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.models.ListeningQuestion
import com.example.data.models.ListeningQuestionType
import com.example.data.models.ListeningTest
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningPracticeScreen(
    uiState: PracticeUiState,
    onAnswerSelected: (Int, String) -> Unit,
    onAudioPlaybackStarted: () -> Unit,
    onAudioPlaybackCompleted: () -> Unit,
    onAudioError: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val test = uiState.selectedListeningTest ?: return
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showSubmitConfirmDialog by remember { mutableStateOf(false) }

    // ExoPlayer initialization
    val exoPlayer = remember(test.audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            if (test.audioUrl.isNotEmpty()) {
                try {
                    val mediaItem = MediaItem.fromUri(Uri.parse(test.audioUrl))
                    setMediaItem(mediaItem)
                    prepare()
                } catch (e: Exception) {
                    onAudioError("Failed to initialize audio stream: ${e.localizedMessage}")
                }
            }
        }
    }

    // Playhead / progress position state
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    onAudioPlaybackCompleted()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                onAudioError("Audio playback error: ${error.localizedMessage ?: "Network or file format issue"}")
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Timer tick update for audio progress
    LaunchedEffect(uiState.isAudioPlaying) {
        while (uiState.isAudioPlaying) {
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            if (exoPlayer.duration > 0) {
                durationMs = exoPlayer.duration
            }
            kotlinx.coroutines.delay(500L)
        }
    }

    if (showSubmitConfirmDialog) {
        val unansweredCount = test.questions.size - uiState.listeningUserAnswers.size
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = { Text("Submit Listening Test?") },
            text = {
                Text(
                    if (unansweredCount > 0)
                        "You have $unansweredCount unanswered question(s). Are you sure you want to submit now?"
                    else
                        "All questions answered. Submit test for scoring?"
                )
            },
            confirmButton = {
                LumenButton(
                    onClick = {
                        showSubmitConfirmDialog = false
                        exoPlayer.stop()
                        onSubmit()
                    },
                    modifier = Modifier.testTag("confirm_submit_button")
                ) {
                    Text("Submit Test")
                }
            },
            dismissButton = {
                LumenTextButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LumenTopBar(
                title = {
                    Column {
                        Text(
    text = test.title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    maxLines = 1
)
                        Text(
                            text = "Listening Module • Difficulty: ${test.difficultyLevel}",
                            style = LumenTheme.typography.labelSmall,
                            color = LumenTheme.colors.primary
                        )
                    }
                },
                navigationIcon = {
                    LumenIconButton(
                        onClick = {
                            exoPlayer.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("listening_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Answered count indicator
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LumenTheme.colors.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
    text = "${uiState.listeningUserAnswers.size}/${test.questions.size} Answered",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                    }
                }
            )
        },
        bottomBar = {
            LumenSurface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val minutes = uiState.listeningTimeRemainingSeconds / 60
                        val seconds = uiState.listeningTimeRemainingSeconds % 60
                        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                        Text(
    text = if (uiState.isTransferTimePhase) "TRANSFER TIME" else "TIME REMAINING",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (uiState.isTransferTimePhase) LumenTheme.colors.tertiary else LumenTheme.colors.onSurfaceVariant
)
                        Text(
    text = timeStr,
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
    color = if (uiState.listeningTimeRemainingSeconds < 180) LumenTheme.colors.error else LumenTheme.colors.onSurface
)
                    }

                    LumenButton(
                        onClick = { showSubmitConfirmDialog = true },
                        modifier = Modifier.testTag("submit_listening_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LumenTheme.colors.primary,
                            contentColor = LumenTheme.colors.onPrimary
                        )
                    ) {
                        Text(
    "Submit Test",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Transfer Time Banner if active
            AnimatedVisibility(visible = uiState.isTransferTimePhase) {
                LumenCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = LumenCardDefaults.cardColors(
                        containerColor = LumenTheme.colors.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = LumenTheme.colors.onTertiaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
    text = "TRANSFER TIME ACTIVE (10 mins)",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onTertiaryContainer
)
                            Text(
                                text = "Audio has concluded. Review and double check your answers on the answer sheet before submitting.",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onTertiaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Audio Player Section (Strict IELTS Single Playback Rule)
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LumenTheme.colors.outlineVariant,
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
    text = "Exam Audio Track",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                        }

                        // Single-play regulation badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LumenTheme.colors.surface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
    text = "Plays ONCE Only",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.audioError != null) {
                        LumenCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.audioError,
                                    style = LumenTheme.typography.bodyMedium,
                                    color = LumenTheme.colors.onErrorContainer
                                )
                            }
                        }
                    } else {
                        // Progress bar (Read-only, NO scrubbing)
                        val progressFraction = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()) else 0f

                        LumenProgressBar(
                            progress = { progressFraction.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LumenTheme.colors.primary,
                            trackColor = LumenTheme.colors.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val currSec = currentPositionMs / 1000
                            val durSec = durationMs / 1000
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", currSec / 60, currSec % 60),
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", durSec / 60, durSec % 60),
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Controls: Play once or status message
                        if (!uiState.hasAudioPlayedOnce) {
                            LumenButton(
                                onClick = {
                                    try {
                                        exoPlayer.play()
                                        onAudioPlaybackStarted()
                                    } catch (e: Exception) {
                                        onAudioError("Failed to start playback: ${e.localizedMessage}")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_audio_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
    "Start Audio (Single Playback)",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                            }
                        } else if (uiState.isAudioPlaying) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenSpinner(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = LumenTheme.colors.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
    text = "Audio Playing... Answer questions below.",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.primary
)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LumenTheme.colors.surface)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
    text = "✓ Audio Playback Finished (IELTS regulations: single play enforced)",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Medium),
    color = LumenTheme.colors.onSurfaceVariant
)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
    text = "Question Sheet",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

            Text(
                text = "Answer all questions while listening or during transfer time.",
                style = LumenTheme.typography.bodySmall,
                color = LumenTheme.colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Questions list
            test.questions.forEach { question ->
                ListeningQuestionCard(
                    question = question,
                    selectedAnswer = uiState.listeningUserAnswers[question.id] ?: "",
                    onAnswerSelected = { answer -> onAnswerSelected(question.id, answer) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ListeningQuestionCard(
    question: ListeningQuestion,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (selectedAnswer.isNotEmpty()) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("listening_question_${question.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LumenTheme.colors.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
    text = "Section ${question.sectionNumber} • Q${question.id}",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LumenTheme.colors.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = question.type.name.replace("_", " "),
                        style = LumenTheme.typography.labelSmall,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
    text = question.questionText,
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)

            // Show form context for FORM_COMPLETION
            if (question.type == ListeningQuestionType.FORM_COMPLETION && question.formContext.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LumenCard(
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
    text = question.formContext,
    style = (LumenTheme.typography.bodyMedium).copy(fontFamily = FontFamily.Monospace),
    modifier = Modifier.padding(12.dp),
    color = LumenTheme.colors.onSurfaceVariant
)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Render inputs depending on Question Type
            when (question.type) {
                ListeningQuestionType.MULTIPLE_CHOICE -> {
                    question.options.forEach { option ->
                        val optionPrefix = option.take(2).trim()
                        val isSelected = selectedAnswer.equals(optionPrefix, ignoreCase = true) || selectedAnswer.equals(option, ignoreCase = true)

                        LumenCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val code = if (option.contains(")")) option.substringBefore(")").trim() else option
                                    onAnswerSelected(code)
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            colors = LumenCardDefaults.cardColors(
                                containerColor = if (isSelected) LumenTheme.colors.primaryContainer.copy(alpha = 0.4f) else LumenTheme.colors.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenRadio(
                                    selected = isSelected,
                                    onClick = {
                                        val code = if (option.contains(")")) option.substringBefore(")").trim() else option
                                        onAnswerSelected(code)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    style = LumenTheme.typography.bodyMedium,
                                    color = LumenTheme.colors.onSurface
                                )
                            }
                        }
                    }
                }

                ListeningQuestionType.FORM_COMPLETION -> {
                    LumenField(
                        value = selectedAnswer,
                        onValueChange = { onAnswerSelected(it) },
                        label = { Text("Type answer for Q${question.id}") },
                        placeholder = { Text("e.g. En-suite") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fill_blank_input_${question.id}"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                ListeningQuestionType.MATCHING -> {
                    Text(
                        text = "Select matching location / option:",
                        style = LumenTheme.typography.labelMedium,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    question.matchingOptions.forEach { matchOpt ->
                        val code = if (matchOpt.contains("-")) matchOpt.substringBefore("-").trim() else matchOpt.take(1)
                        val isSelected = selectedAnswer.equals(code, ignoreCase = true)

                        LumenCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onAnswerSelected(code) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            colors = LumenCardDefaults.cardColors(
                                containerColor = if (isSelected) LumenTheme.colors.primaryContainer.copy(alpha = 0.4f) else LumenTheme.colors.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenRadio(
                                    selected = isSelected,
                                    onClick = { onAnswerSelected(code) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = matchOpt,
                                    style = LumenTheme.typography.bodyMedium,
                                    color = LumenTheme.colors.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
