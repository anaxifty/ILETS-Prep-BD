package com.example.ui.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.QuestionType
import com.example.data.models.ReadingQuestion
import com.example.data.models.ReadingTest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPracticeScreen(
    viewModel: PracticeViewModel,
    uiState: PracticeUiState,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val test = uiState.selectedTest ?: return
    var showConfirmDialog by remember { mutableStateOf(false) }

    val answeredCount = test.questions.count { q ->
        uiState.userAnswers[q.id]?.isNotEmpty() == true
    }

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onSubmitted()
        }
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
                            text = "IELTS Reading Passage • ${answeredCount}/${test.questions.size} Answered",
                            style = LumenTheme.typography.labelSmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    LumenIconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exit_reading_test")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit Test")
                    }
                },
                actions = {
                    // Timer Chip - Emerald Focus Mono Label Style
                    val minutes = uiState.timeRemainingSeconds / 60
                    val seconds = uiState.timeRemainingSeconds % 60
                    val timeString = String.format("%02d:%02d", minutes, seconds)

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (uiState.timeRemainingSeconds < 300) LumenTheme.colors.errorContainer
                                else LumenTheme.colors.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (uiState.timeRemainingSeconds < 300) LumenTheme.colors.error
                                else LumenTheme.colors.outlineVariant,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (uiState.timeRemainingSeconds < 300) LumenTheme.colors.error
                                else LumenTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeString,
                                style = LumenTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (uiState.timeRemainingSeconds < 300) LumenTheme.colors.error
                                else LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    // Submit LumenButton
                    LumenButton(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("submit_test_top_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
    text = "Submit",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold)
)
                    }
                },
                colors = LumenTopBarDefaults.topAppBarColors(
                    containerColor = LumenTheme.colors.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // LumenTab Header (Passage vs Questions)
            LumenTabRow(
                selectedTabIndex = uiState.activeTab,
                containerColor = LumenTheme.colors.surface
            ) {
                LumenTab(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    text = {
                        Text(
    text = "Reading Passage",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                    }
                )
                LumenTab(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    text = {
                        Text(
    text = "Questions (${answeredCount}/${test.questions.size})",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
                    }
                )
            }

            if (uiState.activeTab == 0) {
                // Passage View
                PassageView(
                    passageText = test.passageText,
                    onGoToQuestions = { viewModel.setActiveTab(1) }
                )
            } else {
                // Questions View
                QuestionsView(
                    questions = test.questions,
                    userAnswers = uiState.userAnswers,
                    onAnswerSelected = { qId, ans -> viewModel.onAnswerSelected(qId, ans) },
                    onSubmit = { showConfirmDialog = true }
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
    text = "Submit Reading Test?",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold)
)
            },
            text = {
                Text(
                    text = "You have answered $answeredCount of ${test.questions.size} questions. Are you ready to submit and calculate your Band score?"
                )
            },
            confirmButton = {
                LumenButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.submitTest()
                    },
                    modifier = Modifier.testTag("confirm_submit_button")
                ) {
                    Text("Yes, Submit Now")
                }
            },
            dismissButton = {
                LumenTextButton(onClick = { showConfirmDialog = false }) {
                    Text("Keep Reviewing")
                }
            }
        )
    }
}

@Composable
private fun PassageView(
    passageText: String,
    onGoToQuestions: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LumenCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = LumenTheme.colors.outlineVariant,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                Text(
                    text = passageText,
                    style = LumenTheme.typography.bodyMedium.copy(
                        lineHeight = 26.sp,
                        fontSize = 15.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = LumenTheme.colors.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LumenButton(
            onClick = onGoToQuestions,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(
    text = "Proceed to Questions",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
        }
    }
}

@Composable
private fun QuestionsView(
    questions: List<ReadingQuestion>,
    userAnswers: Map<Int, String>,
    onAnswerSelected: (Int, String) -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        questions.forEachIndexed { index, question ->
            QuestionCard(
                index = index + 1,
                question = question,
                currentAnswer = userAnswers[question.id] ?: "",
                onAnswerSelected = { ans -> onAnswerSelected(question.id, ans) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LumenButton(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_reading_questions_bottom"),
            shape = RoundedCornerShape(50)
        ) {
            Text(
    text = "Submit Test & View Score",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    question: ReadingQuestion,
    currentAnswer: String,
    onAnswerSelected: (String) -> Unit
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (currentAnswer.isNotEmpty()) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant,
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LumenTheme.colors.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
    text = "Question $index",
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
                        text = when (question.type) {
                            QuestionType.MULTIPLE_CHOICE -> "Multiple Choice"
                            QuestionType.TRUE_FALSE_NOT_GIVEN -> "True / False / Not Given"
                            QuestionType.FILL_IN_BLANK -> "Fill in Blank"
                        },
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

            Spacer(modifier = Modifier.height(16.dp))

            when (question.type) {
                QuestionType.MULTIPLE_CHOICE -> {
                    question.options.forEach { option ->
                        val optionKey = option.take(1) // E.g., 'A' from 'A) ...'
                        val isSelected = currentAnswer.equals(optionKey, ignoreCase = true) || currentAnswer.equals(option, ignoreCase = true)

                        LumenOutlinedCard(
                            onClick = { onAnswerSelected(optionKey) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant
                            ),
                            colors = LumenCardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) LumenTheme.colors.primaryContainer.copy(alpha = 0.4f) else LumenTheme.colors.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenRadio(
                                    selected = isSelected,
                                    onClick = { onAnswerSelected(optionKey) }
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

                QuestionType.TRUE_FALSE_NOT_GIVEN -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("TRUE", "FALSE", "NOT GIVEN").forEach { choice ->
                            val isSelected = currentAnswer.equals(choice, ignoreCase = true)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) LumenTheme.colors.primary
                                        else LumenTheme.colors.surfaceVariant
                                    )
                                    .clickable { onAnswerSelected(choice) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
    text = choice,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (isSelected) LumenTheme.colors.onPrimary
                                    else LumenTheme.colors.onSurfaceVariant
)
                            }
                        }
                    }
                }

                QuestionType.FILL_IN_BLANK -> {
                    LumenField(
                        value = currentAnswer,
                        onValueChange = { onAnswerSelected(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type your answer here...") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = LumenFieldDefaults.colors(
                            focusedBorderColor = LumenTheme.colors.primary,
                            unfocusedBorderColor = LumenTheme.colors.outlineVariant
                        )
                    )
                }
            }
        }
    }
}
