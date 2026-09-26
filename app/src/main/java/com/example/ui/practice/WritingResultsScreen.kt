package com.example.ui.practice

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WritingAttempt
import com.example.data.models.WritingCriterionScore

@Composable
fun WritingResultsScreen(
    attempt: WritingAttempt?,
    onReturnToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (attempt == null) return
    val scrollState = rememberScrollState()

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
                            .testTag("return_to_hub_button"),
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
            // Header
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
                        .testTag("results_back_button")
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
    text = "EXAMINER EVALUATION",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                    Text(
    text = attempt.taskTitle.ifEmpty { "IELTS Writing Score Breakdown" },
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
    text = attempt.taskType.uppercase(),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimary
)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
    text = "OVERALL BAND SCORE",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
    text = "%.1f".format(attempt.overallBand),
    style = (LumenTheme.typography.displayLarge).copy(fontWeight = FontWeight.ExtraBold),
    color = LumenTheme.colors.onPrimaryContainer
)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.surface.copy(alpha = 0.8f)
                        ) {
                            Text(
    text = "${attempt.wordCount} words",
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        val mins = attempt.timeTakenSeconds / 60
                        val secs = attempt.timeTakenSeconds % 60
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.surface.copy(alpha = 0.8f)
                        ) {
                            Text(
    text = "%02d:%02d time spent".format(mins, secs),
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                        }
                    }

                    if (attempt.generalFeedback.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
    text = attempt.generalFeedback,
    style = (LumenTheme.typography.bodyMedium).copy(lineHeight = 20.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Criteria Breakdown Header
            Text(
    text = "CRITERION BREAKDOWN",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Criteria Cards
            attempt.criteriaScores.forEach { criterion ->
                CriterionCard(criterion = criterion)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submitted Text / Transcription LumenCard
            LumenCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
    text = if (attempt.inputMethod == "PHOTO_SCAN") "TRANSCRIBED HANDWRITTEN ESSAY" else "SUBMITTED ESSAY TEXT",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                        Text(
                            text = "${attempt.wordCount} Words",
                            style = LumenTheme.typography.labelSmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
    text = attempt.answerText,
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
private fun CriterionCard(criterion: WritingCriterionScore) {
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
                // Mono-label style tag for criterion
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
