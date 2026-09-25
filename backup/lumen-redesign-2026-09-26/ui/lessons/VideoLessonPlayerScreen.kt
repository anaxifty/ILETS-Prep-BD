package com.example.ui.lessons

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.models.VideoLesson
import com.example.data.service.LessonDownloader
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoLessonPlayerScreen(
    lesson: VideoLesson?,
    viewModel: VideoLessonsViewModel,
    uiState: VideoLessonsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (lesson == null) return

    val context = LocalContext.current
    val progress = uiState.lessonProgressMap[lesson.id]

    // Determine if we play local offline file or remote stream
    val localFile = remember(lesson.id) { LessonDownloader(context).getDownloadedFile(lesson.id) }
    val isOfflinePlayback = localFile != null && localFile.exists()
    val videoUri = remember(lesson.id, isOfflinePlayback) {
        if (isOfflinePlayback && localFile != null) Uri.fromFile(localFile)
        else Uri.parse(lesson.videoUrl)
    }

    var isVideoError by remember { mutableStateOf(false) }
    var errorMessageState by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(progress?.lastPositionMs ?: 0L) }
    var totalDurationMs by remember { mutableLongStateOf(progress?.durationMs ?: 0L) }

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(videoUri) {
        val player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            if (progress?.lastPositionMs != null && progress.lastPositionMs > 0L) {
                seekTo(progress.lastPositionMs)
            }
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        totalDurationMs = duration.coerceAtLeast(0L)
                    } else if (playbackState == Player.STATE_ENDED) {
                        viewModel.updateLessonProgress(
                            lessonId = lesson.id,
                            currentPosMs = duration.coerceAtLeast(0L),
                            durationMs = duration.coerceAtLeast(0L)
                        )
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    isVideoError = true
                    errorMessageState = "Video playback failed: ${error.localizedMessage ?: "Codec/Network Error"}"
                }
            })
        }

        exoPlayer = player

        onDispose {
            player.let {
                val finalPos = it.currentPosition.coerceAtLeast(0L)
                val finalDur = it.duration.coerceAtLeast(0L)
                viewModel.updateLessonProgress(lesson.id, finalPos, finalDur)
                it.release()
            }
            exoPlayer = null
        }
    }

    // Periodic progress updates while playing
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(3000)
            exoPlayer?.let { player ->
                if (player.isPlaying) {
                    currentPositionMs = player.currentPosition.coerceAtLeast(0L)
                    totalDurationMs = player.duration.coerceAtLeast(0L)
                    viewModel.updateLessonProgress(lesson.id, currentPositionMs, totalDurationMs)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = lesson.skillCategory.uppercase() + " LESSON",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = lesson.topic.ifEmpty { "Video Lesson" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Playback Source Pill (Offline vs Online)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isOfflinePlayback) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOfflinePlayback) Icons.Default.OfflinePin else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isOfflinePlayback) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOfflinePlayback) "Offline" else "Online",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isOfflinePlayback) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Video Player Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                if (isVideoError) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessageState ?: "Video playback error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                isVideoError = false
                                exoPlayer?.prepare()
                                exoPlayer?.playWhenReady = true
                            }
                        ) {
                            Text("Retry Playback")
                        }
                    }
                } else {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = true
                                setShowNextButton(false)
                                setShowPreviousButton(false)
                                setShowFastForwardButton(true)
                                setShowRewindButton(true)
                                player = exoPlayer
                            }
                        },
                        update = { view ->
                            view.player = exoPlayer
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("video_player_view")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lesson Info & Details Card
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = lesson.skillCategory.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (lesson.duration.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = lesson.duration,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "LESSON SYLLABUS & OVERVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Watch Progress Indicator
                val isCompleted = progress?.completed == true || (totalDurationMs > 0 && currentPositionMs >= totalDurationMs * 0.9)
                val progressPercent = if (totalDurationMs > 0) ((currentPositionMs * 100) / totalDurationMs).toInt() else 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Watch Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = if (isCompleted) "Completed ✓" else "Watched $progressPercent%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        if (totalDurationMs > 0) {
                            CircularProgressIndicator(
                                progress = { if (isCompleted) 1f else progressPercent / 100f },
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
