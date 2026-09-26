package com.example.ui.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.WritingTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(
    uiState: PracticeUiState,
    onTextChanged: (String) -> Unit,
    onPhotoSelected: (Uri?) -> Unit,
    onInputMethodChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val task = uiState.selectedWritingTask ?: return

    val currentText = uiState.writingUserText
    val wordCount = currentText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    val targetWordCount = task.targetWordCount

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
            onInputMethodChanged("PHOTO_SCAN")
        }
    }

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        bottomBar = {
            LumenSurface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = LumenTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Word Count",
                            style = LumenTheme.typography.labelSmall,
                            color = LumenTheme.colors.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
    text = "$wordCount",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = if (wordCount >= targetWordCount) LumenTheme.colors.primary else LumenTheme.colors.onSurface
)
                            Text(
                                text = " / $targetWordCount words target",
                                style = LumenTheme.typography.bodyMedium,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }

                    LumenButton(
                        onClick = onSubmit,
                        enabled = !uiState.isWritingEvaluating && (wordCount > 0 || uiState.writingPhotoUri != null),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("submit_writing_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LumenTheme.colors.primary,
                            contentColor = LumenTheme.colors.onPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
    text = "Submit Essay",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LumenIconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LumenTheme.colors.surfaceVariant)
                                .testTag("writing_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = LumenTheme.colors.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LumenTheme.colors.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
    text = task.taskType.uppercase(),
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onPrimaryContainer
)
                            }
                            Text(
    text = task.title,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                        }
                    }

                    // Timer Pill
                    val mins = uiState.writingTimeRemainingSeconds / 60
                    val secs = uiState.writingTimeRemainingSeconds % 60
                    val timeStr = "%02d:%02d".format(mins, secs)

                    LumenSurface(
                        shape = RoundedCornerShape(20.dp),
                        color = LumenTheme.colors.errorContainer,
                        modifier = Modifier.testTag("writing_timer_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = LumenTheme.colors.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
    text = timeStr,
    style = (LumenTheme.typography.labelLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onErrorContainer
)
                        }
                    }
                }

                // Task Prompt LumenCard
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
    text = "TASK PROMPT",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LumenTheme.colors.surface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
    text = "${task.recommendedTimeMinutes} Mins • ${task.targetWordCount}+ Words",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
    text = task.prompt,
    style = (LumenTheme.typography.bodyMedium).copy(lineHeight = 22.sp),
    color = LumenTheme.colors.onSurfaceVariant
)

                        // If Task 1 has chart image
                        if (!task.imageUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LumenCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                AsyncImage(
                                    model = task.imageUrl,
                                    contentDescription = "Task Chart Diagram",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input Method Selection Tabs
                Text(
    text = "ANSWER SUBMISSION METHOD",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.primary
)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(LumenTheme.colors.surfaceVariant)
                        .padding(4.dp)
                ) {
                    val isText = uiState.writingInputMethod == "TEXT"
                    val isPhoto = uiState.writingInputMethod == "PHOTO_SCAN"

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isText) LumenTheme.colors.surface else Color.Transparent)
                            .clickable { onInputMethodChanged("TEXT") }
                            .padding(vertical = 10.dp)
                            .testTag("method_text_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (isText) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
    text = "Direct Text Input",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = if (isText) FontWeight.Bold else FontWeight.Normal),
    color = if (isText) LumenTheme.colors.onSurface else LumenTheme.colors.onSurfaceVariant
)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPhoto) LumenTheme.colors.surface else Color.Transparent)
                            .clickable { onInputMethodChanged("PHOTO_SCAN") }
                            .padding(vertical = 10.dp)
                            .testTag("method_photo_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = if (isPhoto) LumenTheme.colors.primary else LumenTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
    text = "Photo / Camera Scan",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = if (isPhoto) FontWeight.Bold else FontWeight.Normal),
    color = if (isPhoto) LumenTheme.colors.onSurface else LumenTheme.colors.onSurfaceVariant
)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Section according to method
                if (uiState.writingInputMethod == "TEXT") {
                    LumenField(
                        value = currentText,
                        onValueChange = onTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 280.dp)
                            .testTag("writing_text_input"),
                        placeholder = {
                            Text(
                                text = "Type your complete essay response here. Pay attention to paragraph structure, cohesive devices, vocabulary, and grammar...",
                                style = LumenTheme.typography.bodyMedium
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = LumenFieldDefaults.colors(
                            focusedContainerColor = LumenTheme.colors.surface,
                            unfocusedContainerColor = LumenTheme.colors.surface
                        )
                    )
                } else {
                    // Photo Scan UI
                    LumenCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (uiState.writingPhotoUri != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                ) {
                                    AsyncImage(
                                        model = uiState.writingPhotoUri,
                                        contentDescription = "Scanned Answer Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    LumenIconButton(
                                        onClick = { onPhotoSelected(null) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(CircleShape)
                                            .background(LumenTheme.colors.surface.copy(alpha = 0.8f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove photo"
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
    text = "Handwritten page attached! Gemini AI will transcribe and grade your writing.",
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.primary
)

                                Spacer(modifier = Modifier.height(8.dp))

                                LumenOutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Replace Photo")
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.primary,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
    text = "Upload Handwritten Essay Photo",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Take a clear, well-lit photo of your handwritten paper. Gemini AI reads handwritten answers directly.",
                                    style = LumenTheme.typography.bodySmall,
                                    color = LumenTheme.colors.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LumenButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("upload_photo_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Select Photo from Gallery")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Loading / Evaluating Dialog Overlay
            if (uiState.isWritingEvaluating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    LumenCard(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LumenSpinner(
                                modifier = Modifier.size(56.dp),
                                color = LumenTheme.colors.primary,
                                strokeWidth = 4.dp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
    text = "Reviewing your writing...",
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Senior IELTS AI Examiner is evaluating Task Response, Coherence, Vocabulary, and Grammar...",
                                style = LumenTheme.typography.bodySmall,
                                color = LumenTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
