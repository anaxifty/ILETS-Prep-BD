package com.example.data.service

import android.content.Context
import android.os.StatFs
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

sealed class DownloadResult {
    data class Progress(val percent: Int) : DownloadResult()
    data class Success(val localFile: File) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

class LessonDownloader(private val context: Context) {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val lessonsDir: File
        get() {
            val dir = File(context.filesDir, "video_lessons")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    fun getDownloadedFile(lessonId: String): File? {
        val file = File(lessonsDir, "$lessonId.mp4")
        return if (file.exists() && file.length() > 0) file else null
    }

    fun deleteDownloadedFile(lessonId: String): Boolean {
        val file = File(lessonsDir, "$lessonId.mp4")
        return if (file.exists()) file.delete() else false
    }

    fun downloadVideo(lessonId: String, videoUrl: String): Flow<DownloadResult> = flow {
        // Step 1: Check storage space (minimum 15 MB)
        val minRequiredBytes = 15 * 1024 * 1024L
        val stat = StatFs(context.filesDir.path)
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong

        if (availableBytes < minRequiredBytes) {
            emit(DownloadResult.Error("Not enough storage on device (${availableBytes / (1024 * 1024)} MB available, minimum 15 MB required)"))
            return@flow
        }

        val targetFile = File(lessonsDir, "$lessonId.mp4")

        try {
            emit(DownloadResult.Progress(0))

            val request = Request.Builder().url(videoUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadResult.Error("Video failed to download: HTTP ${response.code}"))
                return@flow
            }

            val body = response.body
            if (body == null) {
                emit(DownloadResult.Error("Video failed to download: Empty response body"))
                return@flow
            }

            val contentLength = body.contentLength()
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(targetFile)

                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var totalBytesRead = 0L
                var lastReportedPercent = -1

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    if (contentLength > 0) {
                        val percent = ((totalBytesRead * 100) / contentLength).toInt()
                        if (percent != lastReportedPercent && percent <= 100) {
                            lastReportedPercent = percent
                            emit(DownloadResult.Progress(percent))
                        }
                    }
                }

                outputStream.flush()
                emit(DownloadResult.Progress(100))
                emit(DownloadResult.Success(targetFile))

            } catch (e: Exception) {
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                emit(DownloadResult.Error("Video failed to download: ${e.localizedMessage ?: "Network read error"}"))
            } finally {
                inputStream?.close()
                outputStream?.close()
            }

        } catch (e: Exception) {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            Log.e("LessonDownloader", "Download error: ${e.message}", e)
            emit(DownloadResult.Error("Video failed to download: ${e.localizedMessage ?: "Connection error"}"))
        }
    }.flowOn(Dispatchers.IO)
}
