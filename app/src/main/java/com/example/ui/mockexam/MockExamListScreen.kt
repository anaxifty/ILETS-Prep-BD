package com.example.ui.mockexam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.MockExam
import com.example.data.models.MockExamAttempt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Feature-band color is resolved inside composables (Emerald Focus brand primary).
val DarkFeatureBandColor: androidx.compose.ui.graphics.Color
  @Composable get() = LumenTheme.colors.primary

@Composable
fun MockExamListScreen(
    viewModel: MockExamViewModel,
    uiState: MockExamUiState,
    onStartExam: (MockExam) -> Unit,
    onViewResult: (MockExamAttempt) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.loadMockExamsData()
    }

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
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LumenTheme.colors.surfaceVariant)
                            .testTag("mock_exam_back_button")
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
    text = "IELTS FULL SITTING",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                        Text(
    text = "Weekly & Monthly Mock Exams",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
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
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LumenSpinner(color = LumenTheme.colors.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading Mock Exams...",
                        style = LumenTheme.typography.bodyMedium,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Active Exam Resume Banner if available
                    val activeAttempt = uiState.activeAttempt
                    if (activeAttempt != null && activeAttempt.status == "IN_PROGRESS") {
                        item {
                            LumenCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("active_exam_resume_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.primaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(LumenTheme.colors.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = LumenTheme.colors.onPrimary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
    text = "Exam in Progress",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                                            Text(
                                                text = "${activeAttempt.examTitle} • Section: ${activeAttempt.currentSection}",
                                                style = LumenTheme.typography.bodySmall,
                                                color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }

                                    LumenButton(
                                        onClick = {
                                            val exam = uiState.mockExams.find { it.id == activeAttempt.mockExamId }
                                                ?: MockExam(id = activeAttempt.mockExamId, title = activeAttempt.examTitle)
                                            viewModel.startOrResumeMockExam(exam)
                                            onStartExam(exam)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Resume")
                                    }
                                }
                            }
                        }
                    }

                    // Scheduled Mock Exams Section
                    item {
                        Text(
    text = "AVAILABLE SCHEDULED SITTINGS",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                    }

                    items(uiState.mockExams, key = { it.id }) { exam ->
                        MockExamCard(
                            exam = exam,
                            activeAttempt = activeAttempt,
                            onStart = {
                                viewModel.startOrResumeMockExam(exam)
                                onStartExam(exam)
                            }
                        )
                    }

                    // Past Results History Section
                    val completedAttempts = uiState.userAttempts.filter { it.status == "COMPLETED" }
                    if (completedAttempts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
    text = "RECENT MOCK EXAM RESULTS",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                        }

                        items(completedAttempts, key = { it.attemptId }) { attempt ->
                            PastMockResultCard(
                                attempt = attempt,
                                onClick = {
                                    viewModel.setSelectedCompletedAttempt(attempt)
                                    onViewResult(attempt)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MockExamCard(
    exam: MockExam,
    activeAttempt: MockExamAttempt?,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    LumenCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mock_exam_card_${exam.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (exam.examType == "WEEKLY") LumenTheme.colors.primaryContainer else LumenTheme.colors.secondaryContainer
                ) {
                    Text(
    text = "${exam.examType} MOCK",
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (exam.examType == "WEEKLY") LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSecondaryContainer
)
                }

                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = LumenTheme.colors.surface
                ) {
                    Text(
                        text = exam.difficultyLevel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = LumenTheme.typography.labelSmall,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
    text = exam.title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = LumenTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "4-Skill Sitting • approx. 2h 40m total duration",
                    style = LumenTheme.typography.bodySmall,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }

            if (exam.startDate.isNotEmpty() && exam.endDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Window: ${exam.startDate} to ${exam.endDate}",
                    style = LumenTheme.typography.labelSmall,
                    color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isCurrentActive = activeAttempt?.mockExamId == exam.id && activeAttempt.status == "IN_PROGRESS"

            LumenButton(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_mock_exam_btn_${exam.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentActive) LumenTheme.colors.tertiary else LumenTheme.colors.primary
                )
            ) {
                Icon(
                    imageVector = if (isCurrentActive) Icons.Default.PlayArrow else Icons.Default.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
    text = if (isCurrentActive) "Resume Exam in Progress" else "Start This Mock Exam",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
            }
        }
    }
}

@Composable
fun PastMockResultCard(
    attempt: MockExamAttempt,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LumenCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("past_result_card_${attempt.attemptId}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
    text = attempt.examTitle,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)

                Spacer(modifier = Modifier.height(4.dp))

                val dateStr = if (attempt.startTimeMillis > 0) {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(attempt.startTimeMillis))
                } else "Recent"

                Text(
                    text = "$dateStr • L: ${attempt.listeningBand} | R: ${attempt.readingBand} | W: ${attempt.writingBand} | S: ${attempt.speakingBand}",
                    style = LumenTheme.typography.bodySmall,
                    color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Overall Band Score LumenBadge
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkFeatureBandColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
    text = "BAND",
    style = (LumenTheme.typography.labelSmall).copy(fontSize = 9.sp),
    color = Color.White.copy(alpha = 0.8f)
)
                    Text(
    text = attempt.overallBand.toString(),
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                }
            }
        }
    }
}
