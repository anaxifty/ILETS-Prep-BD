package com.example.ui.practice

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ReadingTest

@Composable
fun PracticeHubScreen(
    uiState: PracticeUiState,
    onStartReadingTest: (String) -> Unit,
    onStartListeningTest: (String?) -> Unit,
    onStartWritingTask: (String?) -> Unit,
    onStartSpeakingTask: (String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenIconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LumenTheme.colors.surfaceVariant)
                        .testTag("practice_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = LumenTheme.colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
    text = "IELTS PRACTICE HUB",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                    Text(
    text = "Target Skill Modules",
    style = (LumenTheme.typography.headlineSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Baseline Info Banner
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LumenTheme.colors.outline,
                        shape = RoundedCornerShape(26.dp)
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Assessed Reading Level",
                            style = LumenTheme.typography.labelMedium,
                            color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
    text = "Band ${"%.1f".format(uiState.userProfile.readingBand)} Baseline",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recommended passages tailored to your self-assessed target.",
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LumenTheme.colors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = LumenTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
    text = "Skill Practice Modules",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LumenSpinner(color = LumenTheme.colors.primary)
                }
            } else if (uiState.errorMessage != null) {
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.errorContainer)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = LumenTheme.colors.onErrorContainer
                    )
                }
            } else {
                // Pick recommended passage based on user reading band level
                val userBand = uiState.userProfile.readingBand
                val recommendedTest = uiState.readingTests.find { test ->
                    when {
                        userBand < 6.0 -> test.difficultyLevel.equals("Easy", ignoreCase = true)
                        userBand >= 7.5 -> test.difficultyLevel.equals("Hard", ignoreCase = true)
                        else -> test.difficultyLevel.equals("Medium", ignoreCase = true)
                    }
                } ?: uiState.readingTests.firstOrNull()

                // Module 1: Reading (UNLOCKED & ACTIVE)
                SkillModuleCard(
                    title = "Reading Practice",
                    subtitle = "60-Min Real IELTS Format Passage & Questions",
                    icon = Icons.Default.MenuBook,
                    isUnlocked = true,
                    difficulty = recommendedTest?.difficultyLevel ?: "Medium",
                    recommendedTestTitle = recommendedTest?.title,
                    testId = recommendedTest?.id ?: "reading_test_medium_01",
                    buttonLabel = "Start 60-Min Reading Test",
                    onStart = { onStartReadingTest(recommendedTest?.id ?: "reading_test_medium_01") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Additional Reading Passages available
                if (uiState.readingTests.size > 1) {
                    Text(
    text = "All Reading Passages (${uiState.readingTests.size})",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.readingTests.forEach { test ->
                        ReadingPassageRow(
                            test = test,
                            onStart = { onStartReadingTest(test.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Module 2: Listening (UNLOCKED & ACTIVE)
                val listeningBand = uiState.userProfile.listeningBand
                val recListeningTest = uiState.listeningTests.find { test ->
                    when {
                        listeningBand < 6.0 -> test.difficultyLevel.equals("Easy", ignoreCase = true)
                        listeningBand >= 7.5 -> test.difficultyLevel.equals("Hard", ignoreCase = true)
                        else -> test.difficultyLevel.equals("Medium", ignoreCase = true)
                    }
                } ?: uiState.listeningTests.firstOrNull()

                SkillModuleCard(
                    title = "Listening Practice",
                    subtitle = "Audio Streams with Simultaneous Exam Questions & Transfer Time",
                    icon = Icons.Default.Headphones,
                    isUnlocked = true,
                    difficulty = recListeningTest?.difficultyLevel ?: "Medium",
                    recommendedTestTitle = recListeningTest?.title,
                    testId = recListeningTest?.id ?: "",
                    buttonLabel = "Start Listening Test",
                    onStart = { onStartListeningTest(recListeningTest?.id) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Module 3: Writing (UNLOCKED & ACTIVE)
                val recWritingTask = uiState.writingTasks.firstOrNull()

                SkillModuleCard(
                    title = "Writing Practice",
                    subtitle = "Task 1 Visual Charts & Task 2 Essay Prompts with AI Examiner Grading",
                    icon = Icons.Default.EditNote,
                    isUnlocked = true,
                    difficulty = recWritingTask?.difficultyLevel ?: "Medium",
                    recommendedTestTitle = recWritingTask?.title,
                    testId = recWritingTask?.id ?: "",
                    buttonLabel = "Start Writing Practice",
                    onStart = { onStartWritingTask(recWritingTask?.id) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Module 4: Speaking (UNLOCKED & ACTIVE)
                val recSpeakingTask = uiState.speakingTasks.firstOrNull()

                SkillModuleCard(
                    title = "Speaking Practice",
                    subtitle = "Part 1, 2 & 3 Voice Recording with AI Multimodal Pronunciation & Criteria Grading",
                    icon = Icons.Default.Mic,
                    isUnlocked = true,
                    difficulty = recSpeakingTask?.difficultyLevel ?: "Medium",
                    recommendedTestTitle = recSpeakingTask?.title,
                    testId = recSpeakingTask?.id ?: "",
                    buttonLabel = "Start Speaking Practice",
                    onStart = { onStartSpeakingTask(recSpeakingTask?.id) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SkillModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isUnlocked: Boolean,
    badgeText: String? = null,
    difficulty: String? = null,
    recommendedTestTitle: String? = null,
    testId: String = "",
    buttonLabel: String = "Start Test",
    onStart: () -> Unit
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isUnlocked) LumenTheme.colors.primary else LumenTheme.colors.outlineVariant,
                shape = RoundedCornerShape(26.dp)
            )
            .testTag(if (isUnlocked) "${title.lowercase().replace(" ", "_")}_card" else "locked_card_${title.lowercase()}"),
        shape = RoundedCornerShape(26.dp),
        colors = LumenCardDefaults.cardColors(
            containerColor = if (isUnlocked) LumenTheme.colors.surface else LumenTheme.colors.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isUnlocked) LumenTheme.colors.primaryContainer
                                else LumenTheme.colors.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isUnlocked) LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
    text = title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                        Text(
                            text = subtitle,
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isUnlocked) LumenTheme.colors.primaryContainer
                            else LumenTheme.colors.surfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
    text = badgeText ?: difficulty ?: "Available",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (isUnlocked) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant
)
                }
            }

            if (isUnlocked && recommendedTestTitle != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(LumenTheme.colors.surfaceVariant)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
    text = "RECOMMENDED TEST",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
    color = LumenTheme.colors.primary
)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
    text = recommendedTestTitle,
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LumenButton(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_${title.lowercase().replace(" ", "_")}_button"),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LumenTheme.colors.primary,
                        contentColor = LumenTheme.colors.onPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
    text = buttonLabel,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingPassageRow(
    test: ReadingTest,
    onStart: () -> Unit
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() }
            .border(
                width = 1.dp,
                color = LumenTheme.colors.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(LumenTheme.colors.secondaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
    text = test.difficultyLevel,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${test.questions.size} Questions",
                        style = LumenTheme.typography.labelSmall,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
    text = test.title,
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Start Test",
                tint = LumenTheme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
