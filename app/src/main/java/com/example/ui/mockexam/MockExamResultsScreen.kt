package com.example.ui.mockexam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.models.MockExamAttempt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MockExamResultsScreen(
    attempt: MockExamAttempt,
    pastAttempts: List<MockExamAttempt>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        topBar = {
            LumenSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = LumenTheme.colors.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LumenIconButton(
                        onClick = onDone,
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

                    Text(
    text = "Mock Exam Performance",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // High Emphasis Dark Feature Band LumenCard
            item {
                LumenCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overall_band_feature_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = DarkFeatureBandColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
    text = "OVERALL BAND SCORE",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = Color.White.copy(alpha = 0.8f)
)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
    text = attempt.overallBand.toString(),
    style = (LumenTheme.typography.displayLarge).copy(fontWeight = FontWeight.Bold, fontSize = 64.sp),
    color = Color.White
)

                        Text(
                            text = attempt.examTitle,
                            style = LumenTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val delaySeconds = if (attempt.officialTimeUpTimeMillis > 0L && attempt.actualSubmissionTimeMillis > attempt.officialTimeUpTimeMillis) {
                            ((attempt.actualSubmissionTimeMillis - attempt.officialTimeUpTimeMillis) / 1000).coerceAtLeast(0)
                        } else 0L

                        if (delaySeconds > 0) {
                            LumenSurface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFFDAD6),
                                modifier = Modifier.testTag("grace_period_used_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color(0xFFBA1A1A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
    text = "Grace Buffer Used: +${delaySeconds}s after time limit",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFFBA1A1A)
)
                                }
                            }
                        } else {
                            LumenSurface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.testTag("submitted_on_time_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
    text = "Submitted Within Allotted Time",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                                }
                            }
                        }
                    }
                }
            }

            // 4 Skill Sub-Scores Breakdown
            item {
                Text(
    text = "SKILL SUB-SCORES BREAKDOWN",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SkillScoreTile(label = "Listening", score = attempt.listeningBand, modifier = Modifier.weight(1f))
                    SkillScoreTile(label = "Reading", score = attempt.readingBand, modifier = Modifier.weight(1f))
                    SkillScoreTile(label = "Writing", score = attempt.writingBand, modifier = Modifier.weight(1f))
                    SkillScoreTile(label = "Speaking", score = attempt.speakingBand, modifier = Modifier.weight(1f))
                }
            }

            // Trend Comparison against past 3 mock attempts
            item {
                LumenCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trend_comparison_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
    text = "Score Progression (Last 3 Attempts)",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val recentAttempts = pastAttempts.take(3)
                        if (recentAttempts.isEmpty()) {
                            Text(
                                text = "This is your first completed mock exam sitting. Keep practicing to track your score trend!",
                                style = LumenTheme.typography.bodyMedium,
                                color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        } else {
                            recentAttempts.forEachIndexed { idx, prevAttempt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
    text = prevAttempt.examTitle,
    style = (LumenTheme.typography.bodyMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurfaceVariant
)
                                        val dateStr = if (prevAttempt.startTimeMillis > 0) {
                                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(prevAttempt.startTimeMillis))
                                        } else "Attempt #${idx + 1}"
                                        Text(
                                            text = dateStr,
                                            style = LumenTheme.typography.bodySmall,
                                            color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }

                                    LumenSurface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = LumenTheme.colors.primaryContainer
                                    ) {
                                        Text(
    text = "Band ${prevAttempt.overallBand}",
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                LumenButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("return_to_mock_list_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
    text = "Return to Mock Exams Dashboard",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                }
            }
        }
    }
}

@Composable
fun SkillScoreTile(
    label: String,
    score: Double,
    modifier: Modifier = Modifier
) {
    LumenCard(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = LumenTheme.typography.labelSmall,
                color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
    text = score.toString(),
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
        }
    }
}
