package com.example.data.models

data class VideoLesson(
    val id: String = "",
    val title: String = "",
    val skillCategory: String = "", // "Listening", "Reading", "Writing", "Speaking"
    val topic: String = "",
    val description: String = "",
    val duration: String = "", // e.g., "12:45"
    val thumbnailUrl: String = "",
    val videoUrl: String = ""
)

data class LessonProgress(
    val lessonId: String = "",
    val lastPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val completed: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
}

data class DownloadState(
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val progressPercent: Int = 0,
    val localFilePath: String? = null,
    val errorMessage: String? = null
)
