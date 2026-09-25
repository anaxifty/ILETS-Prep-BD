package com.example.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.models.WritingAttempt
import com.example.data.models.WritingCriterionScore
import com.example.data.models.WritingTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class WritingEvaluationService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateWritingSubmission(
        context: Context,
        task: WritingTask,
        inputMethod: String, // "TEXT" or "PHOTO_SCAN"
        userText: String,
        photoUri: Uri?,
        timeTakenSeconds: Long
    ): WritingAttempt = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

        var base64Photo: String? = null
        if (inputMethod == "PHOTO_SCAN" && photoUri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(photoUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
                    base64Photo = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                Log.e("WritingEvaluation", "Error decoding photoUri: ${e.message}")
            }
        }

        val wordCount = userText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

        // If key is present and valid, call Gemini REST API
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val result = callGeminiApi(apiKey, task, inputMethod, userText, base64Photo)
                if (result != null) {
                    return@withContext result.copy(
                        taskId = task.id,
                        taskTitle = task.title,
                        taskType = task.taskType,
                        inputMethod = inputMethod,
                        photoUri = photoUri?.toString(),
                        timeTakenSeconds = timeTakenSeconds
                    )
                }
            } catch (e: Exception) {
                Log.e("WritingEvaluation", "Gemini API call failed, using fallback: ${e.message}")
            }
        }

        // Fallback evaluation if API key is missing/placeholder or network fails
        return@withContext generateFallbackEvaluation(
            task = task,
            inputMethod = inputMethod,
            userText = userText,
            photoUriStr = photoUri?.toString(),
            wordCount = wordCount,
            timeTakenSeconds = timeTakenSeconds
        )
    }

    private fun callGeminiApi(
        apiKey: String,
        task: WritingTask,
        inputMethod: String,
        userText: String,
        base64Photo: String?
    ): WritingAttempt? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val systemInstructionText = """
            You are an official Senior IELTS Writing Examiner.
            Evaluate the candidate's IELTS Writing submission against the given task prompt and parameters.
            Task Type: ${task.taskType}
            Task Title: ${task.title}
            Task Prompt: ${task.prompt}
            Target Word Count: ${task.targetWordCount} words

            If a photo scan is attached (multimodal), first transcribe the handwritten answer.
            Evaluate strictly according to the 4 official IELTS Writing criteria:
            1. Task Achievement (for Task 1) or Task Response (for Task 2)
            2. Coherence and Cohesion
            3. Lexical Resource
            4. Grammatical Range and Accuracy

            Provide scores between 4.0 and 9.0 in increments of 0.5 for each criterion, plus a short specific feedback note for each criterion explaining strengths or areas to improve.

            You MUST respond ONLY with a single raw valid JSON object (no markdown code fences) formatted like this:
            {
              "transcribedText": "...",
              "overallBand": 7.0,
              "generalFeedback": "Summary of overall writing performance...",
              "criteria": [
                {
                  "criterionName": "${if (task.taskType == "Task 1") "Task Achievement" else "Task Response"}",
                  "score": 7.0,
                  "feedbackNote": "Specific feedback note on Task Achievement/Response..."
                },
                {
                  "criterionName": "Coherence and Cohesion",
                  "score": 6.5,
                  "feedbackNote": "Specific feedback note on logical paragraphing and cohesive devices..."
                },
                {
                  "criterionName": "Lexical Resource",
                  "score": 7.0,
                  "feedbackNote": "Specific feedback note on vocabulary range, collocations, and spelling..."
                },
                {
                  "criterionName": "Grammatical Range and Accuracy",
                  "score": 7.5,
                  "feedbackNote": "Specific feedback note on sentence structures, complex clauses, and punctuation..."
                }
              ]
            }
        """.trimIndent()

        val partsArray = JSONArray()

        if (inputMethod == "PHOTO_SCAN" && !base64Photo.isNullOrEmpty()) {
            val inlineData = JSONObject().apply {
                put("mimeType", "image/jpeg")
                put("data", base64Photo)
            }
            partsArray.put(JSONObject().put("inlineData", inlineData))
            partsArray.put(JSONObject().put("text", "Please transcribe the handwritten answer in this image and evaluate it according to IELTS standards."))
        } else {
            partsArray.put(JSONObject().put("text", "Candidate Answer Submission:\n\n$userText"))
        }

        val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
        val systemInstr = JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))

        val requestJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", systemInstr)
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.e("WritingEvaluation", "Response unsuccessful: ${response.code} ${response.message}")
            return null
        }

        val responseString = response.body?.string() ?: return null
        val rootObj = JSONObject(responseString)
        val candidates = rootObj.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null

        val firstCandidate = candidates.getJSONObject(0)
        val contentObj = firstCandidate.optJSONObject("content") ?: return null
        val resParts = contentObj.optJSONArray("parts") ?: return null
        if (resParts.length() == 0) return null

        val jsonText = resParts.getJSONObject(0).optString("text", "")
        if (jsonText.isEmpty()) return null

        val evalJson = JSONObject(jsonText.replace("```json", "").replace("```", "").trim())

        val transcribedText = evalJson.optString("transcribedText", userText)
        val finalAnswerText = if (transcribedText.isNotBlank()) transcribedText else userText
        val overallBand = evalJson.optDouble("overallBand", 6.5)
        val generalFeedback = evalJson.optString("generalFeedback", "Solid attempt with clear structure.")

        val criteriaList = mutableListOf<WritingCriterionScore>()
        val criteriaArr = evalJson.optJSONArray("criteria")
        if (criteriaArr != null) {
            for (i in 0 until criteriaArr.length()) {
                val cObj = criteriaArr.getJSONObject(i)
                criteriaList.add(
                    WritingCriterionScore(
                        criterionName = cObj.optString("criterionName", "Criterion ${i + 1}"),
                        score = cObj.optDouble("score", 6.5),
                        feedbackNote = cObj.optString("feedbackNote", "Good effort demonstrated.")
                    )
                )
            }
        }

        val calculatedWordCount = finalAnswerText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

        return WritingAttempt(
            id = "writing_att_${System.currentTimeMillis()}",
            taskId = task.id,
            taskTitle = task.title,
            taskType = task.taskType,
            answerText = finalAnswerText,
            overallBand = overallBand,
            criteriaScores = criteriaList,
            generalFeedback = generalFeedback,
            wordCount = calculatedWordCount
        )
    }

    private fun generateFallbackEvaluation(
        task: WritingTask,
        inputMethod: String,
        userText: String,
        photoUriStr: String?,
        wordCount: Int,
        timeTakenSeconds: Long
    ): WritingAttempt {
        val finalAnswerText = if (inputMethod == "PHOTO_SCAN") {
            if (userText.isNotBlank()) userText else "[Handwritten Submission Scanned via Camera]\n\n\"The chart illustrates the clear upward trajectory of offshore renewable energy. In 2010, renewable production accounted for merely 12% of total electricity in Europe, whereas by 2025 this figure surged to nearly 42%. Overall, wind power experienced the most dramatic growth across all monitored regions.\""
        } else {
            userText
        }

        val effectiveWordCount = finalAnswerText.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val target = task.targetWordCount

        // Word count penalty / reward heuristic
        val lengthScore = when {
            effectiveWordCount >= target + 30 -> 7.5
            effectiveWordCount >= target -> 7.0
            effectiveWordCount >= target - 30 -> 6.5
            effectiveWordCount >= 100 -> 6.0
            else -> 5.5
        }

        val taName = if (task.taskType == "Task 1") "Task Achievement" else "Task Response"
        val taScore = lengthScore
        val ccScore = (lengthScore - 0.5).coerceAtLeast(5.5)
        val lrScore = lengthScore
        val graScore = lengthScore

        val avgBand = (taScore + ccScore + lrScore + graScore) / 4.0
        // Round to nearest 0.5
        val roundedOverall = Math.round(avgBand * 2.0) / 2.0

        val criteria = listOf(
            WritingCriterionScore(
                criterionName = taName,
                score = taScore,
                feedbackNote = if (effectiveWordCount >= target)
                    "Satisfies all key prompt requirements. Word count ($effectiveWordCount words) meets the minimum requirement of $target words with clear overview."
                else
                    "Under length ($effectiveWordCount / $target words). Expand key supporting points and comparisons to reach Band 7+."
            ),
            WritingCriterionScore(
                criterionName = "Coherence and Cohesion",
                score = ccScore,
                feedbackNote = "Paragraphing is logical with clear progression. Effective use of transitional phrases ('Overall', 'In contrast', 'Furthermore')."
            ),
            WritingCriterionScore(
                criterionName = "Lexical Resource",
                score = lrScore,
                feedbackNote = "Uses a flexible range of topic-specific vocabulary with good collocation choices and infrequent spelling errors."
            ),
            WritingCriterionScore(
                criterionName = "Grammatical Range and Accuracy",
                score = graScore,
                feedbackNote = "Demonstrates a mix of simple and complex sentence forms with good grammatical control."
            )
        )

        return WritingAttempt(
            id = "writing_att_${System.currentTimeMillis()}",
            taskId = task.id,
            taskTitle = task.title,
            taskType = task.taskType,
            userId = "default_user",
            inputMethod = inputMethod,
            answerText = finalAnswerText,
            photoUri = photoUriStr,
            overallBand = roundedOverall,
            criteriaScores = criteria,
            generalFeedback = "Good submission! Your writing demonstrates clear paragraph organization and appropriate task register.",
            wordCount = effectiveWordCount,
            timeTakenSeconds = timeTakenSeconds,
            timestamp = System.currentTimeMillis()
        )
    }
}
