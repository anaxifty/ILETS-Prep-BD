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
import com.example.data.models.ListeningQuestion
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningResultsScreen(
    uiState: PracticeUiState,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attempt = uiState.currentListeningAttempt ?: return
    val test = uiState.selectedListeningTest
    val scrollState = rememberScrollState()

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LumenTopBar(
                title = { Text(
    "Listening Test Results",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
) },
                navigationIcon = {
                    LumenIconButton(
                        onClick = onBackToHub,
                        modifier = Modifier.testTag("results_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Score LumenCard
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
                    containerColor = LumenTheme.colors.primaryContainer
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
                            .background(LumenTheme.colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = LumenTheme.colors.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "IELTS Listening Band",
                        style = LumenTheme.typography.labelMedium,
                        color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Text(
    text = "Band ${"%.1f".format(attempt.bandScore)}",
    style = (LumenTheme.typography.headlineMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
    text = "${attempt.score} / ${attempt.totalQuestions}",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            Text(
                                text = "Raw Score",
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        LumenDivider(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp),
                            color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.2f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val mins = attempt.timeTakenSeconds / 60
                            val secs = attempt.timeTakenSeconds % 60
                            val timeStr = String.format(Locale.getDefault(), "%dm %02ds", mins, secs)

                            Text(
    text = timeStr,
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            Text(
                                text = "Time Spent",
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
    text = "Question & Answer Breakdown",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    modifier = Modifier.fillMaxWidth()
)

            Spacer(modifier = Modifier.height(12.dp))

            test?.questions?.forEach { question ->
                val userAnswer = attempt.userAnswers[question.id.toString()] ?: ""
                val isCorrect = userAnswer.equals(question.correctAnswer.trim(), ignoreCase = true)

                ListeningQuestionResultCard(
                    question = question,
                    userAnswer = userAnswer,
                    isCorrect = isCorrect
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LumenButton(
                onClick = onBackToHub,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("back_to_hub_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
    "Return to Practice Hub",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ListeningQuestionResultCard(
    question: ListeningQuestion,
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
                        .background(LumenTheme.colors.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
    text = "Q${question.id} • ${question.type.name.replace("_", " ")}",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCorrect) Color(0xFFCDF4E0) else Color(0xFFFFDAD6))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF1B8A5A) else Color(0xFFBA1A1A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
    text = if (isCorrect) "Correct" else "Incorrect",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (isCorrect) Color(0xFF1B8A5A) else Color(0xFFBA1A1A)
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
    text = question.questionText,
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold)
)

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LumenTheme.colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Text(
    text = "Your Answer: ${if (userAnswer.isEmpty()) "No answer provided" else userAnswer}",
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.SemiBold),
    color = if (isCorrect) Color(0xFF1B8A5A) else LumenTheme.colors.error
)

                if (!isCorrect) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
    text = "Correct Answer: ${question.correctAnswer}",
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFF1B8A5A)
)
                }
            }

            if (question.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explanation: ${question.explanation}",
                    style = LumenTheme.typography.bodySmall,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
