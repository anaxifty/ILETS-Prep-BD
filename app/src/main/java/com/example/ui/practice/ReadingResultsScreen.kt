package com.example.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
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
import com.example.data.models.ReadingQuestion

@Composable
fun ReadingResultsScreen(
    uiState: PracticeUiState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attempt = uiState.currentAttempt
    val test = uiState.selectedTest

    if (attempt == null || test == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LumenSpinner()
        }
        return
    }

    val scrollState = rememberScrollState()

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(LumenTheme.colors.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
    text = "READING TEST COMPLETED",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
    text = "Performance Breakdown",
    style = (LumenTheme.typography.headlineMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

            Text(
                text = test.title,
                style = LumenTheme.typography.bodyMedium,
                color = LumenTheme.colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Score Banner LumenCard - Emerald Focus Style
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LumenTheme.colors.primary,
                        shape = RoundedCornerShape(26.dp)
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(LumenTheme.colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = LumenTheme.colors.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
    text = "Estimated Band ${"%.1f".format(attempt.bandScore)}",
    style = (LumenTheme.typography.headlineLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
    text = "Raw Score: ${attempt.score} / ${attempt.totalQuestions} Correct",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)

                    Spacer(modifier = Modifier.height(16.dp))

                    val minutes = attempt.timeTakenSeconds / 60
                    val seconds = attempt.timeTakenSeconds % 60
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(LumenTheme.colors.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = LumenTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
    text = "Time Spent: ${minutes}m ${seconds}s",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurfaceVariant
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
    text = "Per-Question Analysis",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

            Spacer(modifier = Modifier.height(12.dp))

            test.questions.forEachIndexed { index, question ->
                val userAnswer = attempt.userAnswers[question.id.toString()] ?: ""
                val isCorrect = userAnswer.equals(question.correctAnswer.trim(), ignoreCase = true)

                ResultQuestionItem(
                    index = index + 1,
                    question = question,
                    userAnswer = userAnswer,
                    isCorrect = isCorrect
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LumenButton(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("results_done_button"),
                shape = RoundedCornerShape(50)
            ) {
                Text(
    text = "Return to Practice Hub",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultQuestionItem(
    index: Int,
    question: ReadingQuestion,
    userAnswer: String,
    isCorrect: Boolean
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isCorrect) Color(0xFF1B8A5A) else LumenTheme.colors.error,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isCorrect) Color(0xFFCDF4E0) else LumenTheme.colors.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF1B8A5A) else LumenTheme.colors.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
    text = "Question $index",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCorrect) Color(0xFFCDF4E0) else LumenTheme.colors.errorContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
    text = if (isCorrect) "CORRECT" else "INCORRECT",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (isCorrect) Color(0xFF1B8A5A) else LumenTheme.colors.error
)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
    text = question.questionText,
    style = (LumenTheme.typography.bodyMedium).copy(fontWeight = FontWeight.Medium),
    color = LumenTheme.colors.onSurface
)

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LumenTheme.colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Row {
                    Text(
    text = "Your Answer: ",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)
                    Text(
    text = userAnswer.ifEmpty { "(No answer provided)" },
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (isCorrect) Color(0xFF1B8A5A) else LumenTheme.colors.error
)
                }

                if (!isCorrect) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
    text = "Correct Answer: ",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)
                        Text(
    text = question.correctAnswer,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFF1B8A5A)
)
                    }
                }
            }

            if (question.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Explanation: ${question.explanation}",
                    style = LumenTheme.typography.bodySmall,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
