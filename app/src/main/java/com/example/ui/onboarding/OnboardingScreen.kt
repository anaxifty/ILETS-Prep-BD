package com.example.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.EditNote
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    uiState: OnboardingUiState,
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isCompleted) {
        onOnboardingFinished()
    }

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        topBar = {
            LumenTopBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
    text = "Setup Profile",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                        // Step Indicator Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(3) { step ->
                                Box(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .width(if (uiState.currentStep == step) 24.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (uiState.currentStep == step) LumenTheme.colors.primary
                                            else LumenTheme.colors.outline
                                        )
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (uiState.currentStep > 0) {
                        LumenIconButton(
                            onClick = { viewModel.previousStep() },
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = LumenTopBarDefaults.topAppBarColors(
                    containerColor = LumenTheme.colors.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (uiState.currentStep) {
                    0 -> TargetBandStep(
                        selectedBand = uiState.targetBand,
                        onSelectBand = { viewModel.setTargetBand(it) }
                    )
                    1 -> TestDateStep(
                        selectedDateOption = uiState.testDateOption,
                        onSelectOption = { viewModel.setTestDate(it) }
                    )
                    2 -> SelfAssessmentStep(
                        listening = uiState.listeningBand,
                        reading = uiState.readingBand,
                        writing = uiState.writingBand,
                        speaking = uiState.speakingBand,
                        onListeningChanged = { viewModel.updateListeningBand(it) },
                        onReadingChanged = { viewModel.updateReadingBand(it) },
                        onWritingChanged = { viewModel.updateWritingBand(it) },
                        onSpeakingChanged = { viewModel.updateSpeakingBand(it) }
                    )
                }
            }

            // Bottom Navigation CTA LumenButton
            LumenButton(
                onClick = { viewModel.nextStep() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
                    .testTag("onboarding_next_button"),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LumenTheme.colors.primary,
                    contentColor = LumenTheme.colors.onPrimary
                )
            ) {
                if (uiState.isLoading) {
                    LumenSpinner(
                        modifier = Modifier.size(24.dp),
                        color = LumenTheme.colors.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
    text = if (uiState.currentStep == 2) "Complete & Start Learning" else "Continue",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TargetBandStep(
    selectedBand: Double,
    onSelectBand: (Double) -> Unit
) {
    val bands = listOf(5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(LumenTheme.colors.primaryContainer)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
    text = "STEP 1 OF 3 • TARGET SCORE",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "What is your target overall band score?",
            style = LumenTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = (-0.5).sp
            ),
            color = LumenTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Most universities and migration visas require Band 6.5 to 7.5.",
            style = LumenTheme.typography.bodyMedium,
            color = LumenTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            items(bands) { band ->
                val isSelected = band == selectedBand
                LumenCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectBand(band) }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.outline,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(
                        containerColor = if (isSelected) LumenTheme.colors.primaryContainer else LumenTheme.colors.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
    text = "Band $band",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = if (isSelected) LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSurface
)
                            Text(
                                text = getBandDescription(band),
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestDateStep(
    selectedDateOption: String,
    onSelectOption: (String) -> Unit
) {
    val options = listOf(
        "Within 1 Month" to "Upcoming test date soon",
        "In 3 Months" to "Standard preparation schedule",
        "In 6 Months" to "Comprehensive long-term plan",
        "Flexible / Undecided" to "Learning at custom pace"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(LumenTheme.colors.primaryContainer)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
    text = "STEP 2 OF 3 • TIMELINE",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "When do you plan to take the exam?",
            style = LumenTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = (-0.5).sp
            ),
            color = LumenTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This helps us pace your daily practice modules and mock exam schedules.",
            style = LumenTheme.typography.bodyMedium,
            color = LumenTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { (option, subtitle) ->
            val isSelected = option == selectedDateOption
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectOption(option) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.outline,
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = if (isSelected) LumenTheme.colors.primaryContainer else LumenTheme.colors.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = if (isSelected) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
    text = option,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = if (isSelected) LumenTheme.colors.onPrimaryContainer else LumenTheme.colors.onSurface
)
                            Text(
                                text = subtitle,
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = LumenTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelfAssessmentStep(
    listening: Double,
    reading: Double,
    writing: Double,
    speaking: Double,
    onListeningChanged: (Double) -> Unit,
    onReadingChanged: (Double) -> Unit,
    onWritingChanged: (Double) -> Unit,
    onSpeakingChanged: (Double) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(LumenTheme.colors.primaryContainer)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
    text = "STEP 3 OF 3 • SELF ASSESSMENT",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Assess your current skill levels",
            style = LumenTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = (-0.5).sp
            ),
            color = LumenTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set your estimated starting band score for each of the 4 IELTS skills.",
            style = LumenTheme.typography.bodyMedium,
            color = LumenTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        SkillSliderCard(
            title = "Listening",
            icon = Icons.Default.Headphones,
            value = listening,
            onValueChange = onListeningChanged,
            testTag = "listening_slider"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SkillSliderCard(
            title = "Reading",
            icon = Icons.Default.MenuBook,
            value = reading,
            onValueChange = onReadingChanged,
            testTag = "reading_slider"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SkillSliderCard(
            title = "Writing",
            icon = Icons.Default.EditNote,
            value = writing,
            onValueChange = onWritingChanged,
            testTag = "writing_slider"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SkillSliderCard(
            title = "Speaking",
            icon = Icons.Default.Mic,
            value = speaking,
            onValueChange = onSpeakingChanged,
            testTag = "speaking_slider"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SkillSliderCard(
    title: String,
    icon: ImageVector,
    value: Double,
    onValueChange: (Double) -> Unit,
    testTag: String
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LumenTheme.colors.outline,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(
            containerColor = LumenTheme.colors.surface
        ),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LumenTheme.colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = LumenTheme.colors.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
    text = title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                }

                // Band Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LumenTheme.colors.secondaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
    text = "Band ${"%.1f".format(value)}",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LumenSlider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toDouble()) },
                valueRange = 1.0f..9.0f,
                steps = 15, // 0.5 increments between 1.0 and 9.0
                modifier = Modifier.testTag(testTag),
                colors = LumenSliderDefaults.colors(
                    thumbColor = LumenTheme.colors.primary,
                    activeTrackColor = LumenTheme.colors.primary,
                    inactiveTrackColor = LumenTheme.colors.outline
                )
            )
        }
    }
}

fun getBandDescription(band: Double): String {
    return when (band) {
        5.5 -> "Modest User"
        6.0 -> "Competent User"
        6.5 -> "Competent +"
        7.0 -> "Good User"
        7.5 -> "Very Good User"
        8.0 -> "Expert User"
        8.5 -> "Near Native"
        9.0 -> "Expert Native"
        else -> "Custom Goal"
    }
}
