package com.example.ui.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.DownloadStatus
import com.example.data.models.VideoLesson

@Composable
fun VideoLessonsListScreen(
    viewModel: VideoLessonsViewModel,
    uiState: VideoLessonsUiState,
    onSelectLesson: (VideoLesson) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    val skillCategories = listOf("All", "Listening", "Reading", "Writing", "Speaking")

    val filteredLessons = if (uiState.selectedSkill == "All") {
        uiState.lessons
    } else {
        uiState.lessons.filter { it.skillCategory.equals(uiState.selectedSkill, ignoreCase = true) }
    }

    val lessonsBySkill = filteredLessons.groupBy { it.skillCategory }

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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LumenIconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.surfaceVariant)
                                .testTag("video_lessons_back_button")
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
    text = "IELTS VIDEO LESSONS",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                            Text(
    text = "Passive Learning Library",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Skill Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(skillCategories) { category ->
                            val isSelected = uiState.selectedSkill == category
                            LumenChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedSkill(category) },
                                label = {
                                    Text(
    text = category,
    style = LumenTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
)
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = LumenChipDefaults.filterChipColors(
                                    selectedContainerColor = LumenTheme.colors.primary,
                                    selectedLabelColor = LumenTheme.colors.onPrimary,
                                    containerColor = LumenTheme.colors.surfaceVariant,
                                    labelColor = LumenTheme.colors.onSurfaceVariant
                                )
                            )
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
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LumenSpinner(color = LumenTheme.colors.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading Video Lessons...",
                        style = LumenTheme.typography.bodyMedium,
                        color = LumenTheme.colors.onSurfaceVariant
                    )
                }
            } else if (filteredLessons.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
    text = "No lessons found for '${uiState.selectedSkill}'",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try switching filters to view other skill modules.",
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
                    // Global error message card if present
                    if (uiState.errorMessage != null) {
                        item {
                            LumenCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.errorContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = LumenTheme.colors.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = uiState.errorMessage ?: "",
                                            style = LumenTheme.typography.bodyMedium,
                                            color = LumenTheme.colors.onErrorContainer
                                        )
                                    }
                                    LumenIconButton(onClick = { viewModel.clearError() }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss error",
                                            tint = LumenTheme.colors.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    lessonsBySkill.forEach { (skillName, lessonList) ->
                        item {
                            Text(
    text = "$skillName Skill Lessons".uppercase(),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)
                        }

                        items(lessonList, key = { it.id }) { lesson ->
                            val progress = uiState.lessonProgressMap[lesson.id]
                            val downloadState = uiState.downloadStateMap[lesson.id]

                            LessonRowCard(
                                lesson = lesson,
                                progress = progress,
                                downloadState = downloadState,
                                onSelect = {
                                    viewModel.selectLessonForPlayback(lesson)
                                    onSelectLesson(lesson)
                                },
                                onDownload = {
                                    viewModel.downloadLesson(context, lesson)
                                },
                                onDeleteDownload = {
                                    viewModel.deleteDownloadedLesson(context, lesson.id)
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
fun LessonRowCard(
    lesson: VideoLesson,
    progress: com.example.data.models.LessonProgress?,
    downloadState: com.example.data.models.DownloadState?,
    onSelect: () -> Unit,
    onDownload: () -> Unit,
    onDeleteDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    // House Rules: soft-stone card style (surfaceVariant, 8dp radius)
    LumenCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("video_lesson_card_${lesson.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = LumenCardDefaults.cardColors(
            containerColor = LumenTheme.colors.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Signature 22dp media-card radius for thumbnail
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(LumenTheme.colors.surface)
                ) {
                    if (lesson.thumbnailUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(lesson.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = lesson.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Play Overlay Icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = LumenTheme.colors.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Duration badge bottom right
                    if (lesson.duration.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
    text = lesson.duration,
    style = (LumenTheme.typography.labelSmall).copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
    color = Color.White
)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Category & Topic Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categoryColor = when (lesson.skillCategory.lowercase()) {
                            "listening" -> LumenTheme.colors.primaryContainer
                            "reading" -> LumenTheme.colors.secondaryContainer
                            "writing" -> LumenTheme.colors.tertiaryContainer
                            else -> LumenTheme.colors.surface
                        }
                        val categoryTextColor = when (lesson.skillCategory.lowercase()) {
                            "listening" -> LumenTheme.colors.onPrimaryContainer
                            "reading" -> LumenTheme.colors.onSecondaryContainer
                            "writing" -> LumenTheme.colors.onTertiaryContainer
                            else -> LumenTheme.colors.onSurface
                        }

                        LumenSurface(
                            shape = RoundedCornerShape(6.dp),
                            color = categoryColor
                        ) {
                            Text(
    text = lesson.skillCategory.uppercase(),
    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
    style = (LumenTheme.typography.labelSmall).copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
    color = categoryTextColor
)
                        }

                        if (lesson.topic.isNotEmpty()) {
                            Text(
    text = lesson.topic,
    style = (LumenTheme.typography.labelSmall).copy(fontSize = 11.sp),
    color = LumenTheme.colors.onSurfaceVariant,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
    text = lesson.title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurfaceVariant,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = lesson.description,
                        style = LumenTheme.typography.bodySmall,
                        color = LumenTheme.colors.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Bar: Progress status + Download status button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Watch Progress Status Indicator
                val isCompleted = progress?.completed == true
                val lastPosMs = progress?.lastPositionMs ?: 0L
                val durMs = progress?.durationMs ?: 0L
                val progressPercent = if (durMs > 0) ((lastPosMs * 100) / durMs).toInt() else 0

                when {
                    isCompleted -> {
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
    text = "Completed",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            }
                        }
                    }
                    progressPercent > 0 -> {
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.secondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenSpinner(
                                    progress = { progressPercent / 100f },
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = LumenTheme.colors.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
    text = "In Progress ($progressPercent%)",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSecondaryContainer
)
                            }
                        }
                    }
                    else -> {
                        LumenSurface(
                            shape = RoundedCornerShape(10.dp),
                            color = LumenTheme.colors.surface
                        ) {
                            Text(
                                text = "Not Started",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }

                // Download Status Action Pill
                val status = downloadState?.status ?: DownloadStatus.NOT_DOWNLOADED

                when (status) {
                    DownloadStatus.NOT_DOWNLOADED -> {
                        LumenOutlinedButton(
                            onClick = onDownload,
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("download_lesson_button_${lesson.id}"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download for offline",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
    text = "Download",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold)
)
                        }
                    }

                    DownloadStatus.DOWNLOADING -> {
                        val percent = downloadState?.progressPercent ?: 0
                        LumenSurface(
                            shape = RoundedCornerShape(20.dp),
                            color = LumenTheme.colors.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LumenSpinner(
                                    progress = { percent / 100f },
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = LumenTheme.colors.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
    text = "Downloading $percent%",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            }
                        }
                    }

                    DownloadStatus.DOWNLOADED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LumenSurface(
                                shape = RoundedCornerShape(20.dp),
                                color = LumenTheme.colors.tertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = "Downloaded Offline",
                                        tint = LumenTheme.colors.onTertiaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
    text = "Downloaded",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onTertiaryContainer
)
                                }
                            }

                            LumenIconButton(
                                onClick = onDeleteDownload,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete download",
                                    tint = LumenTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    DownloadStatus.ERROR -> {
                        LumenOutlinedButton(
                            onClick = onDownload,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = LumenTheme.colors.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Retry download",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
    text = "Retry",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold)
)
                        }
                    }
                }
            }
        }
    }
}
