package com.example.ui.lessons

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.DownloadState
import com.example.data.models.DownloadStatus
import com.example.data.models.LessonProgress
import com.example.data.models.VideoLesson
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.service.DownloadResult
import com.example.data.service.LessonDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VideoLessonsUiState(
    val lessons: List<VideoLesson> = emptyList(),
    val selectedSkill: String = "All", // "All", "Listening", "Reading", "Writing", "Speaking"
    val lessonProgressMap: Map<String, LessonProgress> = emptyMap(),
    val downloadStateMap: Map<String, DownloadState> = emptyMap(),
    val currentPlayingLesson: VideoLesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VideoLessonsViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoLessonsUiState())
    val uiState: StateFlow<VideoLessonsUiState> = _uiState.asStateFlow()

    fun loadData(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val lessons = firestoreRepository.fetchVideoLessons()
                val profile = userPreferencesRepository.userProfileFlow.first()
                val userId = profile.phone.ifEmpty { "default_user" }
                val progressMap = firestoreRepository.fetchUserLessonProgress(userId)

                val downloader = LessonDownloader(context)
                val initialDownloadStates = mutableMapOf<String, DownloadState>()

                lessons.forEach { lesson ->
                    val localFile = downloader.getDownloadedFile(lesson.id)
                    if (localFile != null) {
                        initialDownloadStates[lesson.id] = DownloadState(
                            status = DownloadStatus.DOWNLOADED,
                            progressPercent = 100,
                            localFilePath = localFile.absolutePath
                        )
                    } else {
                        initialDownloadStates[lesson.id] = DownloadState(
                            status = DownloadStatus.NOT_DOWNLOADED
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        lessons = lessons,
                        lessonProgressMap = progressMap,
                        downloadStateMap = initialDownloadStates,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load video lessons: ${e.message}"
                    )
                }
            }
        }
    }

    fun setSelectedSkill(skill: String) {
        _uiState.update { it.copy(selectedSkill = skill) }
    }

    fun downloadLesson(context: Context, lesson: VideoLesson) {
        val downloader = LessonDownloader(context)

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedMap = state.downloadStateMap.toMutableMap()
                updatedMap[lesson.id] = DownloadState(
                    status = DownloadStatus.DOWNLOADING,
                    progressPercent = 0
                )
                state.copy(downloadStateMap = updatedMap)
            }

            downloader.downloadVideo(lesson.id, lesson.videoUrl).collect { result ->
                when (result) {
                    is DownloadResult.Progress -> {
                        _uiState.update { state ->
                            val updatedMap = state.downloadStateMap.toMutableMap()
                            updatedMap[lesson.id] = DownloadState(
                                status = DownloadStatus.DOWNLOADING,
                                progressPercent = result.percent
                            )
                            state.copy(downloadStateMap = updatedMap)
                        }
                    }
                    is DownloadResult.Success -> {
                        _uiState.update { state ->
                            val updatedMap = state.downloadStateMap.toMutableMap()
                            updatedMap[lesson.id] = DownloadState(
                                status = DownloadStatus.DOWNLOADED,
                                progressPercent = 100,
                                localFilePath = result.localFile.absolutePath
                            )
                            state.copy(downloadStateMap = updatedMap)
                        }
                    }
                    is DownloadResult.Error -> {
                        _uiState.update { state ->
                            val updatedMap = state.downloadStateMap.toMutableMap()
                            updatedMap[lesson.id] = DownloadState(
                                status = DownloadStatus.ERROR,
                                errorMessage = result.message
                            )
                            state.copy(
                                downloadStateMap = updatedMap,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteDownloadedLesson(context: Context, lessonId: String) {
        val downloader = LessonDownloader(context)
        downloader.deleteDownloadedFile(lessonId)
        _uiState.update { state ->
            val updatedMap = state.downloadStateMap.toMutableMap()
            updatedMap[lessonId] = DownloadState(status = DownloadStatus.NOT_DOWNLOADED)
            state.copy(downloadStateMap = updatedMap)
        }
    }

    fun selectLessonForPlayback(lesson: VideoLesson) {
        _uiState.update { it.copy(currentPlayingLesson = lesson) }
    }

    fun updateLessonProgress(lessonId: String, currentPosMs: Long, durationMs: Long) {
        if (durationMs <= 0L) return
        viewModelScope.launch {
            val profile = userPreferencesRepository.userProfileFlow.first()
            val userId = profile.phone.ifEmpty { "default_user" }

            val existingProgress = _uiState.value.lessonProgressMap[lessonId]
            val maxPos = maxOf(currentPosMs, existingProgress?.lastPositionMs ?: 0L)
            val isCompleted = existingProgress?.completed == true || (durationMs > 0 && maxPos >= (durationMs * 0.9))

            val newProgress = LessonProgress(
                lessonId = lessonId,
                lastPositionMs = maxPos,
                durationMs = durationMs,
                completed = isCompleted,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )

            _uiState.update { state ->
                val updatedMap = state.lessonProgressMap.toMutableMap()
                updatedMap[lessonId] = newProgress
                state.copy(lessonProgressMap = updatedMap)
            }

            firestoreRepository.saveUserLessonProgress(userId, newProgress)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
