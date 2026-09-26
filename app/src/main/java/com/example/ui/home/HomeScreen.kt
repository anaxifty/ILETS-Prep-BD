package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.theme.AmberGold

@Composable
fun HomeScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onSignOut: () -> Unit,
    onNavigateToPractice: () -> Unit = {},
    onNavigateToVideoLessons: () -> Unit = {},
    onNavigateToMockExams: () -> Unit = {},
    onNavigateToCenterLocator: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfileState by userPreferencesRepository.userProfileFlow.collectAsState(initial = UserProfile())

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar - Emerald Focus Aesthetic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LumenTheme.colors.primaryContainer)
                            .border(2.dp, LumenTheme.colors.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
    text = "BD",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Assalamu Alaikum 👋",
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                        Text(
    text = if (userProfileState.phone.isNotEmpty()) userProfileState.phone else "IELTS Student",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                    }
                }

                LumenIconButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LumenTheme.colors.surfaceVariant)
                        .testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = LumenTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== HERO: Goal-gradient band progress card (Emerald Focus) =====
            val baseline = userProfileState.overallStartingBand
            val target = userProfileState.targetBand
            val latestAvg = listOf(
                userProfileState.readingBand,
                userProfileState.listeningBand,
                userProfileState.writingBand,
                userProfileState.speakingBand
            ).filter { it > 0 }.average().takeIf { it > 0 } ?: baseline
            val progress = if (target > baseline) {
                ((latestAvg - baseline) / (target - baseline)).toFloat().coerceIn(0f, 1f)
            } else 0f
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("band_progress_hero_card"),
                shape = RoundedCornerShape(26.dp),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    LumenTheme.colors.primary,
                                    LumenTheme.colors.secondary
                                )
                            )
                        )
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BandProgressRing(progress = progress)
                    Spacer(modifier = Modifier.width(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
    text = "Target Band ${"%.1f".format(target)} Roadmap",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (progress >= 1f)
                                "Goal reached — keep it sharp! 🏆"
                            else
                                "Now Band ${"%.1f".format(latestAvg)} • +${"%.1f".format(maxOf(0.0, target - latestAvg))} to go. You're ${(progress * 100).toInt()}% there.",
                            style = LumenTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid Cards - Emerald Focus (Mint & Paper Container Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Target Band LumenCard
                LumenCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .testTag("target_band_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = LumenCardDefaults.cardColors(
                        containerColor = LumenTheme.colors.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
    text = "Band ${"%.1f".format(userProfileState.targetBand)}",
    style = (LumenTheme.typography.headlineSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            Text(
                                text = "Target Goal",
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Next Milestone LumenCard
                LumenCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .testTag("milestone_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = LumenCardDefaults.cardColors(
                        containerColor = LumenTheme.colors.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = LumenTheme.colors.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
    text = userProfileState.testDate,
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                            Text(
                                text = "Exam Timeline",
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Overall Starting Estimate LumenCard
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
                    containerColor = LumenTheme.colors.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Starting Estimated Level",
                            style = LumenTheme.typography.labelMedium,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
    text = "Overall Band ${"%.1f".format(userProfileState.overallStartingBand)}",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LumenTheme.colors.surfaceVariant)
                            .border(1.dp, LumenTheme.colors.outlineVariant, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
    text = "Initial Baseline",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Section: Your 4 Skills (chunked navigation, Miller's Law) =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
    text = "Practice your 4 skills",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

                LumenTextButton(
                    onClick = onNavigateToPractice,
                    modifier = Modifier.testTag("open_practice_hub_button")
                ) {
                    Text(
    text = "Practice Hub →",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SkillProgressRow(
                    name = "Reading",
                    icon = Icons.Default.MenuBook,
                    currentBand = userProfileState.readingBand,
                    targetBand = userProfileState.targetBand,
                    onClick = onNavigateToPractice
                )
                SkillProgressRow(
                    name = "Listening",
                    icon = Icons.Default.Headphones,
                    currentBand = userProfileState.listeningBand,
                    targetBand = userProfileState.targetBand,
                    onClick = onNavigateToPractice
                )
                SkillProgressRow(
                    name = "Writing",
                    icon = Icons.Default.EditNote,
                    currentBand = userProfileState.writingBand,
                    targetBand = userProfileState.targetBand,
                    onClick = onNavigateToPractice
                )
                SkillProgressRow(
                    name = "Speaking",
                    icon = Icons.Default.Mic,
                    currentBand = userProfileState.speakingBand,
                    targetBand = userProfileState.targetBand,
                    onClick = onNavigateToPractice
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Section: Level up & book in person =====
            Text(
    text = "Level up & book in person",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)

            Spacer(modifier = Modifier.height(12.dp))

            // Video Lessons Passive Learning LumenCard
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToVideoLessons() }
                    .border(
                        width = 1.dp,
                        color = LumenTheme.colors.outlineVariant,
                        shape = RoundedCornerShape(26.dp)
                    )
                    .testTag("video_lessons_banner_card"),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(LumenTheme.colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = null,
                                tint = LumenTheme.colors.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
    text = "Video Lessons Library",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                            Text(
                                text = "Watch strategy lessons, download for offline",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Video Lessons",
                        tint = LumenTheme.colors.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mock Exam Full Sitting LumenCard (Dark Feature Band Treatment)
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMockExams() }
                    .testTag("mock_exam_banner_card"),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
    text = "Weekly & Monthly Mock Exams",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                            Text(
                                text = "Full 4-skill exam, timed exactly like the real thing",
                                style = LumenTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Mock Exams",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Find Partner Practice Center Locator LumenCard
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCenterLocator() }
                    .testTag("partner_center_locator_home_card"),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.surfaceVariant
                ),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(LumenTheme.colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
    text = "Find Partner Practice Center",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                            Text(
                                text = "Book in-person practice mock tests & coaching sessions near you",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Find Center",
                        tint = LumenTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Partner Coaching Center info strip (replaces duplicate locator card)
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
                    containerColor = LumenTheme.colors.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCenterLocator() }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(LumenTheme.colors.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = LumenTheme.colors.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
    text = "Partner Center Mock Slots",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                        Text(
                            text = "Find practice mock test slots at coaching centers near Dhaka, CTG & Sylhet.",
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BandProgressRing(progress: Float) {
    // Goal-gradient effect: seeing how close you are to the target accelerates effort.
    val trackColor = Color.White.copy(alpha = 0.25f)
    val arcColor = AmberGold
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 7.dp.toPx()
            val inset = stroke / 2
            drawArc(
                color = trackColor,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke)
            )
            drawArc(
                color = arcColor,
                startAngle = -90f, sweepAngle = 360f * progress.coerceIn(0.02f, 1f), useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke)
            )
        }
        Text(
    text = "${(progress * 100).toInt()}%",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.ExtraBold),
    color = Color.White
)
    }
}

@Composable
fun SkillProgressRow(
    name: String,
    icon: ImageVector,
    currentBand: Double,
    targetBand: Double,
    onClick: (() -> Unit)? = null
) {
    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .border(
                width = 1.dp,
                color = LumenTheme.colors.outline,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(
            containerColor = LumenTheme.colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
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

                Column {
                    Text(
    text = name,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                    Text(
                        text = "Current: Band ${"%.1f".format(currentBand)} / Target ${"%.1f".format(targetBand)}",
                        style = LumenTheme.typography.bodySmall,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Mini band progress bar (immediate visual feedback loop)
                    val frac = if (targetBand > 0) (currentBand / targetBand).toFloat().coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LumenTheme.colors.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(frac)
                                .height(6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            LumenTheme.colors.primary,
                                            LumenTheme.colors.secondary
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            // Gap Indicator Chip
            val gap = targetBand - currentBand
            val gapText = if (gap <= 0) "Goal Reached" else "+${"%.1f".format(gap)} needed"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (gap <= 0) LumenTheme.colors.primaryContainer
                        else LumenTheme.colors.surfaceVariant
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
    text = gapText,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (gap <= 0) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant
)
            }
        }
    }
}
