package com.example.ui.mockexam

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ListeningQuestion
import com.example.data.models.ListeningQuestionType
import java.util.Locale
import com.example.data.models.MockExamAttempt
import com.example.data.models.QuestionType
import com.example.data.models.ReadingQuestion
import kotlinx.coroutines.delay

@Composable
fun MockExamFlowScreen(
    viewModel: MockExamViewModel,
    uiState: MockExamUiState,
    onExamCompleted: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attempt = uiState.activeAttempt ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LumenSpinner()
        }
        return
    }

    // Continuous timer calculated from server/start timestamp
    var elapsedSeconds by remember {
        mutableStateOf(((System.currentTimeMillis() - attempt.startTimeMillis) / 1000).coerceAtLeast(0))
    }

    val totalExamSeconds = (uiState.selectedMockExam?.totalDurationMinutes ?: 160) * 60
    val gracePeriodSeconds = 180 // 3-minute grace buffer
    val officialTimeUpTimeMillis = attempt.startTimeMillis + (totalExamSeconds * 1000L)

    val remainingSeconds = (totalExamSeconds - elapsedSeconds).coerceAtLeast(0)
    val isOfficialTimeExpired = elapsedSeconds >= totalExamSeconds
    val graceRemainingSeconds = (totalExamSeconds + gracePeriodSeconds - elapsedSeconds).coerceAtLeast(0)
    val isGracePeriodExpired = elapsedSeconds >= (totalExamSeconds + gracePeriodSeconds)

    // Ticking loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds = ((System.currentTimeMillis() - attempt.startTimeMillis) / 1000).coerceAtLeast(0)
        }
    }

    val currentSection = attempt.currentSection

    // Section user input states
    val listeningAnswers = remember { mutableStateMapOf<Int, String>() }
    val readingAnswers = remember { mutableStateMapOf<Int, String>() }
    var writingEssayText by remember { mutableStateOf(attempt.writingEssayText) }
    var speakingRecorded by remember { mutableStateOf(attempt.speakingAudioUri.isNotEmpty()) }

    // Auto-submit when grace period expires
    LaunchedEffect(isGracePeriodExpired) {
        if (isGracePeriodExpired && attempt.status == "IN_PROGRESS") {
            val listeningBand = calculateListeningBand(uiState.currentListeningTest?.questions ?: emptyList(), listeningAnswers)
            val readingBand = calculateReadingBand(uiState.currentReadingTest?.questions ?: emptyList(), readingAnswers)
            val writingBand = calculateWritingBand(writingEssayText)
            val speakingBand = if (speakingRecorded) 7.0 else 6.0

            val now = System.currentTimeMillis()
            val finalAttempt = attempt.copy(
                listeningBand = listeningBand,
                readingBand = readingBand,
                writingBand = writingBand,
                speakingBand = speakingBand,
                officialTimeUpTimeMillis = officialTimeUpTimeMillis,
                actualSubmissionTimeMillis = now
            )
            viewModel.finalizeMockExam(finalAttempt)
            onExamCompleted()
        }
    }

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LumenSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    color = DarkFeatureBandColor,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
    text = "MOCK EXAM IN PROGRESS",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color.White.copy(alpha = 0.8f)
)
                                Text(
    text = attempt.examTitle,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                            }

                            // Continuous Live Timer / Grace Timer LumenBadge
                            LumenSurface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isOfficialTimeExpired) LumenTheme.colors.error else Color.White.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isOfficialTimeExpired) Icons.Default.Warning else Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
    text = if (isOfficialTimeExpired) "Grace: ${formatTime(graceRemainingSeconds)}" else formatTime(remainingSeconds),
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = Color.White,
    modifier = Modifier.testTag("exam_continuous_timer")
)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Stepper for 4 Skills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SkillStepPill(label = "1. Listening", isCurrent = currentSection.contains("LISTENING"), isDone = isSectionDone("LISTENING", currentSection))
                            SkillStepPill(label = "2. Reading", isCurrent = currentSection.contains("READING"), isDone = isSectionDone("READING", currentSection))
                            SkillStepPill(label = "3. Writing", isCurrent = currentSection.contains("WRITING"), isDone = isSectionDone("WRITING", currentSection))
                            SkillStepPill(label = "4. Speaking", isCurrent = currentSection.contains("SPEAKING"), isDone = isSectionDone("SPEAKING", currentSection))
                        }
                    }
                }

                // Official Time-Up Grace Period Warning Banner
                if (isOfficialTimeExpired && !isGracePeriodExpired) {
                    LumenSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grace_period_warning_banner"),
                        color = LumenTheme.colors.errorContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Time Up Warning",
                                tint = LumenTheme.colors.onErrorContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
    text = "EXAM TIME IS UP!",
    style = (LumenTheme.typography.labelLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onErrorContainer
)
                                Text(
                                    text = "3-minute grace period active (${formatTime(graceRemainingSeconds)} remaining) to finish current answer. Auto-submitting when buffer expires.",
                                    style = LumenTheme.typography.bodySmall,
                                    color = LumenTheme.colors.onErrorContainer
                                )
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
            when {
                currentSection == "LISTENING" -> {
                    MockListeningSection(
                        test = uiState.currentListeningTest,
                        answers = listeningAnswers,
                        onAnswerChanged = { qId, ans -> listeningAnswers[qId] = ans },
                        onSubmitSection = {
                            val band = calculateListeningBand(uiState.currentListeningTest?.questions ?: emptyList(), listeningAnswers)
                            val updated = attempt.copy(listeningBand = band)
                            viewModel.updateActiveAttemptSection("TRANSITION_READING", updated)
                        }
                    )
                }

                currentSection == "TRANSITION_READING" -> {
                    MockTransitionBreakScreen(
                        completedSectionName = "Listening",
                        completedSectionBand = attempt.listeningBand,
                        nextSectionName = "Reading",
                        nextSectionDurationMinutes = 60,
                        onStartNext = {
                            viewModel.updateActiveAttemptSection("READING", attempt)
                        }
                    )
                }

                currentSection == "READING" -> {
                    MockReadingSection(
                        test = uiState.currentReadingTest,
                        answers = readingAnswers,
                        onAnswerChanged = { qId, ans -> readingAnswers[qId] = ans },
                        onSubmitSection = {
                            val band = calculateReadingBand(uiState.currentReadingTest?.questions ?: emptyList(), readingAnswers)
                            val updated = attempt.copy(readingBand = band)
                            viewModel.updateActiveAttemptSection("TRANSITION_WRITING", updated)
                        }
                    )
                }

                currentSection == "TRANSITION_WRITING" -> {
                    MockTransitionBreakScreen(
                        completedSectionName = "Reading",
                        completedSectionBand = attempt.readingBand,
                        nextSectionName = "Writing",
                        nextSectionDurationMinutes = 60,
                        onStartNext = {
                            viewModel.updateActiveAttemptSection("WRITING", attempt)
                        }
                    )
                }

                currentSection == "WRITING" -> {
                    MockWritingSection(
                        task = uiState.currentWritingTask,
                        essayText = writingEssayText,
                        onEssayChanged = { writingEssayText = it },
                        onSubmitSection = {
                            val band = calculateWritingBand(writingEssayText)
                            val updated = attempt.copy(
                                writingBand = band,
                                writingEssayText = writingEssayText
                            )
                            viewModel.updateActiveAttemptSection("TRANSITION_SPEAKING", updated)
                        }
                    )
                }

                currentSection == "TRANSITION_SPEAKING" -> {
                    MockTransitionBreakScreen(
                        completedSectionName = "Writing",
                        completedSectionBand = attempt.writingBand,
                        nextSectionName = "Speaking",
                        nextSectionDurationMinutes = 14,
                        onStartNext = {
                            viewModel.updateActiveAttemptSection("SPEAKING", attempt)
                        }
                    )
                }

                currentSection == "SPEAKING" -> {
                    MockSpeakingSection(
                        task = uiState.currentSpeakingTask,
                        isRecorded = speakingRecorded,
                        onToggleRecording = { speakingRecorded = !speakingRecorded },
                        onSubmitExam = {
                            val speakingBand = if (speakingRecorded) 7.0 else 6.0
                            val finalAttempt = attempt.copy(
                                speakingBand = speakingBand,
                                speakingAudioUri = if (speakingRecorded) "mock_audio_recorded" else ""
                            )
                            viewModel.finalizeMockExam(finalAttempt)
                            onExamCompleted()
                        }
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Exam Completed")
                    }
                }
            }
        }
    }
}

@Composable
fun SkillStepPill(
    label: String,
    isCurrent: Boolean,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    LumenSurface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = when {
            isCurrent -> LumenTheme.colors.primary
            isDone -> Color(0xFF1B8A5A)
            else -> Color.White.copy(alpha = 0.2f)
        }
    ) {
        Text(
    text = label,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
    color = Color.White,
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
)
    }
}

private fun isSectionDone(section: String, currentSection: String): Boolean {
    val order = listOf("LISTENING", "READING", "WRITING", "SPEAKING", "COMPLETED")
    val currentBase = when {
        currentSection.contains("LISTENING") -> "LISTENING"
        currentSection.contains("READING") -> "READING"
        currentSection.contains("WRITING") -> "WRITING"
        currentSection.contains("SPEAKING") -> "SPEAKING"
        else -> "COMPLETED"
    }
    return order.indexOf(section) < order.indexOf(currentBase)
}

@Composable
fun MockTransitionBreakScreen(
    completedSectionName: String,
    completedSectionBand: Double,
    nextSectionName: String,
    nextSectionDurationMinutes: Int,
    onStartNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(LumenTheme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LumenTheme.colors.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
    text = "$completedSectionName Section Complete!",
    style = (LumenTheme.typography.headlineSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
    text = "Estimated Section Score: Band $completedSectionBand",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.primary
)

        Spacer(modifier = Modifier.height(24.dp))

        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
    text = "NEXT SECTION: ${nextSectionName.uppercase()}",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
    text = "Allowed Duration: $nextSectionDurationMinutes Minutes",
    style = (LumenTheme.typography.bodyLarge).copy(fontWeight = FontWeight.Medium),
    color = LumenTheme.colors.onSurfaceVariant
)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Take a short 2-minute transition break. Remember, the continuous exam clock is running.",
                    style = LumenTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LumenButton(
            onClick = onStartNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("begin_next_section_btn"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
    text = "Begin $nextSectionName Section Now",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
        }
    }
}

@Composable
fun MockListeningSection(
    test: com.example.data.models.ListeningTest?,
    answers: Map<Int, String>,
    onAnswerChanged: (Int, String) -> Unit,
    onSubmitSection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
    text = "SECTION 1: LISTENING",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

        Text(
    text = test?.title ?: "Listening Section",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = LumenTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
    text = "Official Recording Playing",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold)
)
                    Text(
                        text = "Listen carefully and answer the questions below.",
                        style = LumenTheme.typography.bodySmall
                    )
                }
            }
        }

        val questions = test?.questions ?: emptyList()
        questions.forEach { question ->
            LumenCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
    text = "Q${question.id}. ${question.questionText}",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                    if (!question.formContext.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.surfaceVariant
                        ) {
                            Text(
                                text = question.formContext,
                                modifier = Modifier.padding(10.dp),
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val selected = answers[question.id] ?: ""

                    if (question.type == ListeningQuestionType.MULTIPLE_CHOICE || question.type == ListeningQuestionType.MATCHING) {
                        val options = question.options.ifEmpty { question.matchingOptions }
                        options.forEach { opt ->
                            val optKey = opt.take(1)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenRadio(
                                    selected = selected == optKey || selected == opt,
                                    onClick = { onAnswerChanged(question.id, optKey) }
                                )
                                Text(
                                    text = opt,
                                    style = LumenTheme.typography.bodyMedium,
                                    color = LumenTheme.colors.onSurface
                                )
                            }
                        }
                    } else {
                        LumenField(
                            value = selected,
                            onValueChange = { onAnswerChanged(question.id, it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("listening_ans_input_${question.id}"),
                            placeholder = { Text("Enter answer...") },
                            singleLine = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LumenButton(
            onClick = onSubmitSection,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_listening_section_btn"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
    "Submit Listening & Proceed to Reading",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
        }
    }
}

@Composable
fun MockReadingSection(
    test: com.example.data.models.ReadingTest?,
    answers: Map<Int, String>,
    onAnswerChanged: (Int, String) -> Unit,
    onSubmitSection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
    text = "SECTION 2: READING",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

        Text(
    text = test?.title ?: "Reading Passage",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

        // Passage card
        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
    text = "PASSAGE TEXT",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = test?.passageText ?: "",
                    style = LumenTheme.typography.bodyMedium,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }
        }

        val questions = test?.questions ?: emptyList()
        questions.forEach { question ->
            LumenCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
    text = "Q${question.id}. ${question.questionText}",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                    Spacer(modifier = Modifier.height(12.dp))

                    val selected = answers[question.id] ?: ""

                    if (question.type == QuestionType.MULTIPLE_CHOICE || question.type == QuestionType.TRUE_FALSE_NOT_GIVEN) {
                        question.options.forEach { opt ->
                            val optKey = opt.take(1)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenRadio(
                                    selected = selected == optKey || selected == opt,
                                    onClick = { onAnswerChanged(question.id, if (question.type == QuestionType.TRUE_FALSE_NOT_GIVEN) opt else optKey) }
                                )
                                Text(
                                    text = opt,
                                    style = LumenTheme.typography.bodyMedium,
                                    color = LumenTheme.colors.onSurface
                                )
                            }
                        }
                    } else {
                        LumenField(
                            value = selected,
                            onValueChange = { onAnswerChanged(question.id, it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reading_ans_input_${question.id}"),
                            placeholder = { Text("Enter answer...") },
                            singleLine = true
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LumenButton(
            onClick = onSubmitSection,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_reading_section_btn"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
    "Submit Reading & Proceed to Writing",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
        }
    }
}

@Composable
fun MockWritingSection(
    task: com.example.data.models.WritingTask?,
    essayText: String,
    onEssayChanged: (String) -> Unit,
    onSubmitSection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val wordCount = remember(essayText) {
        if (essayText.isBlank()) 0 else essayText.trim().split("\\s+".toRegex()).size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
    text = "SECTION 3: WRITING",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

        Text(
    text = task?.title ?: "Writing Task",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
    text = "TASK PROMPT",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task?.prompt ?: "",
                    style = LumenTheme.typography.bodyMedium,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
    text = "Your Essay Response",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)

            LumenSurface(
                shape = RoundedCornerShape(10.dp),
                color = if (wordCount >= (task?.targetWordCount ?: 250)) LumenTheme.colors.primaryContainer else LumenTheme.colors.surfaceVariant
            ) {
                Text(
    text = "$wordCount / ${task?.targetWordCount ?: 250} words",
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = if (wordCount >= (task?.targetWordCount ?: 250)) LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSurfaceVariant
)
            }
        }

        LumenField(
            value = essayText,
            onValueChange = onEssayChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .testTag("mock_writing_essay_input"),
            placeholder = { Text("Write your full essay here...") },
            shape = RoundedCornerShape(10.dp)
        )

        LumenButton(
            onClick = onSubmitSection,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_writing_section_btn"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
    "Submit Writing & Proceed to Speaking",
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
)
        }
    }
}

@Composable
fun MockSpeakingSection(
    task: com.example.data.models.SpeakingTask?,
    isRecorded: Boolean,
    onToggleRecording: () -> Unit,
    onSubmitExam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
    text = "SECTION 4: SPEAKING",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

        Text(
    text = task?.title ?: "Speaking Interview",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
    text = "PART 2 CUE CARD",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
    text = task?.part2CueCard ?: "Describe a memorable experience...",
    style = (LumenTheme.typography.bodyLarge).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurfaceVariant
)

                task?.part2Bullets?.forEach { bullet ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• $bullet",
                        style = LumenTheme.typography.bodyMedium,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Record Audio Simulation Component
        LumenCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = LumenCardDefaults.cardColors(
                containerColor = if (isRecorded) LumenTheme.colors.primaryContainer else LumenTheme.colors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LumenIconButton(
                    onClick = onToggleRecording,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isRecorded) LumenTheme.colors.primary else LumenTheme.colors.error)
                        .testTag("record_speaking_btn")
                ) {
                    Icon(
                        imageVector = if (isRecorded) Icons.Default.Check else Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
    text = if (isRecorded) "Audio Response Recorded" else "Tap to Record Speaking Answer",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = if (isRecorded) LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSurface
)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LumenButton(
            onClick = onSubmitExam,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("finish_full_mock_exam_btn"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkFeatureBandColor)
        ) {
            Text(
    text = "FINISH OFFICIAL MOCK EXAM SITTING",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}

private fun calculateListeningBand(questions: List<ListeningQuestion>, answers: Map<Int, String>): Double {
    if (questions.isEmpty()) return 6.5
    var correct = 0
    questions.forEach { q ->
        val userAns = answers[q.id]?.trim()?.lowercase(Locale.getDefault()) ?: ""
        val correctAns = q.correctAnswer.trim().lowercase(Locale.getDefault())
        if (userAns.isNotEmpty() && (userAns == correctAns || correctAns.contains(userAns))) {
            correct++
        }
    }
    val ratio = correct.toDouble() / questions.size
    return when {
        ratio >= 0.8 -> 7.5
        ratio >= 0.6 -> 6.5
        ratio >= 0.4 -> 5.5
        else -> 5.0
    }
}

private fun calculateReadingBand(questions: List<ReadingQuestion>, answers: Map<Int, String>): Double {
    if (questions.isEmpty()) return 6.5
    var correct = 0
    questions.forEach { q ->
        val userAns = answers[q.id]?.trim()?.lowercase(Locale.getDefault()) ?: ""
        val correctAns = q.correctAnswer.trim().lowercase(Locale.getDefault())
        if (userAns.isNotEmpty() && (userAns == correctAns || correctAns.contains(userAns))) {
            correct++
        }
    }
    val ratio = correct.toDouble() / questions.size
    return when {
        ratio >= 0.8 -> 7.5
        ratio >= 0.6 -> 6.5
        ratio >= 0.4 -> 5.5
        else -> 5.0
    }
}

private fun calculateWritingBand(essay: String): Double {
    val words = if (essay.isBlank()) 0 else essay.trim().split("\\s+".toRegex()).size
    return when {
        words >= 250 -> 7.0
        words >= 180 -> 6.5
        words >= 100 -> 5.5
        else -> 5.0
    }
}
