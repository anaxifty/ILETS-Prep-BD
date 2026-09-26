package com.example.ui.practice

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SpeakingAttempt
import com.example.data.models.SpeakingCriterionScore
import java.io.File

@Composable
fun SpeakingResultsScreen(
    attempt: SpeakingAttempt?,
    onReturnToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (attempt == null) return
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    DisposableEffect(attempt.audioFilePath) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        bottomBar = {
            LumenSurface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = LumenTheme.colors.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LumenButton(
                        onClick = onReturnToHub,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("return_to_hub_speaking_button"),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LumenTheme.colors.primary,
                            contentColor = LumenTheme.colors.onPrimary
                        )
                    ) {
                        Text(
    text = "Return to Practice Hub",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
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
                .padding(20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenIconButton(
                    onClick = onReturnToHub,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LumenTheme.colors.surfaceVariant)
                        .testTag("speaking_results_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LumenTheme.colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
    text = "IELTS SPEAKING EVALUATION",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                    Text(
    text = attempt.taskTitle.ifEmpty { "Part ${attempt.partNumber} Evaluation" },
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Overall Band Hero Banner
            LumenCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(LumenTheme.colors.primary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
    text = "PART ${attempt.partNumber}",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimary
)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
    text = "OVERALL SPEAKING BAND SCORE",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
    text = "%.1f".format(attempt.overallBand),
    style = (LumenTheme.typography.displayLarge).copy(fontWeight = FontWeight.ExtraBold),
    color = LumenTheme.colors.onPrimaryContainer
)

                    if (attempt.generalFeedback.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
    text = attempt.generalFeedback,
    style = (LumenTheme.typography.bodyMedium).copy(lineHeight = 20.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Playback Player LumenCard (if audio file exists)
            val audioPath = attempt.audioFilePath
            val audioFile = if (!audioPath.isNullOrEmpty()) File(audioPath) else null
            if (audioFile != null && audioFile.exists()) {
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LumenIconButton(
                                onClick = {
                                    if (isPlayingAudio) {
                                        mediaPlayer?.pause()
                                        isPlayingAudio = false
                                    } else {
                                        if (mediaPlayer == null) {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(audioFile.absolutePath)
                                                prepare()
                                                setOnCompletionListener { isPlayingAudio = false }
                                            }
                                        }
                                        mediaPlayer?.start()
                                        isPlayingAudio = true
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(LumenTheme.colors.primary)
                                    .testTag("play_speaking_audio_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play recording",
                                    tint = LumenTheme.colors.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
    text = "Your Recorded Audio Response",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                                Text(
                                    text = "Listen to your recorded delivery alongside examiner notes",
                                    style = LumenTheme.typography.bodySmall,
                                    color = LumenTheme.colors.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 4 Criteria Breakdown Header
            Text(
    text = "IELTS CRITERIA SCORES",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

            Spacer(modifier = Modifier.height(12.dp))

            attempt.criteriaScores.forEach { criterion ->
                SpeakingCriterionCard(criterion = criterion)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pronunciation Methodology & Flag Note
            if (attempt.pronunciationAssessmentNote.isNotEmpty()) {
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
    text = "PRONUNCIATION ASSESSMENT NOTE",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
    text = attempt.pronunciationAssessmentNote,
    style = (LumenTheme.typography.bodySmall).copy(lineHeight = 18.sp),
    color = LumenTheme.colors.onSurfaceVariant
)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Spoken Transcription LumenCard
            LumenCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
    text = "SPOKEN RESPONSE TRANSCRIPTION",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
    text = attempt.transcription,
    style = (LumenTheme.typography.bodyMedium).copy(lineHeight = 22.sp),
    color = LumenTheme.colors.onSurfaceVariant
)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SpeakingCriterionCard(criterion: SpeakingCriterionScore) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LumenTheme.colors.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = LumenTheme.colors.secondaryContainer
                ) {
                    Text(
    text = criterion.criterionName.uppercase(),
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
    color = LumenTheme.colors.onSecondaryContainer
)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Band",
                        style = LumenTheme.typography.labelSmall,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
    text = "%.1f".format(criterion.score),
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.ExtraBold),
    color = LumenTheme.colors.primary
)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
    text = criterion.feedbackNote,
    style = (LumenTheme.typography.bodySmall).copy(lineHeight = 18.sp),
    color = LumenTheme.colors.onSurface
)
        }
    }
}
