package com.example.data.models

data class MockExam(
    val id: String = "",
    val title: String = "",
    val examType: String = "WEEKLY", // "WEEKLY" or "MONTHLY"
    val difficultyLevel: String = "Standard",
    val startDate: String = "",
    val endDate: String = "",
    val listeningTestId: String = "listening_test_medium_01",
    val readingTestId: String = "reading_test_medium_01",
    val writingTaskId: String = "writing_task_02",
    val speakingTaskId: String = "speaking_task_01",
    val totalDurationMinutes: Int = 160
)

data class MockExamAttempt(
    val attemptId: String = "",
    val mockExamId: String = "",
    val userId: String = "",
    val examTitle: String = "",
    val examType: String = "WEEKLY",
    val startTimeMillis: Long = System.currentTimeMillis(),
    val endTimeMillis: Long = 0L,
    val status: String = "IN_PROGRESS", // "IN_PROGRESS", "COMPLETED"
    val currentSection: String = "LISTENING", // "LISTENING", "TRANSITION_READING", "READING", "TRANSITION_WRITING", "WRITING", "TRANSITION_SPEAKING", "SPEAKING", "COMPLETED"
    val listeningBand: Double = 0.0,
    val readingBand: Double = 0.0,
    val writingBand: Double = 0.0,
    val speakingBand: Double = 0.0,
    val overallBand: Double = 0.0,
    val listeningAnswersJson: String = "",
    val readingAnswersJson: String = "",
    val writingEssayText: String = "",
    val speakingAudioUri: String = "",
    val officialTimeUpTimeMillis: Long = 0L,
    val actualSubmissionTimeMillis: Long = 0L
)

object IeltsScoreCalculator {
    /**
     * Calculates the overall IELTS band score according to the official rounding rules:
     * - Average ending in < .25 rounds DOWN to .0
     * - Average ending in >= .25 and < .75 rounds to .5
     * - Average ending in >= .75 rounds UP to next integer .0
     */
    fun calculateOverallBand(
        listening: Double,
        reading: Double,
        writing: Double,
        speaking: Double
    ): Double {
        val average = (listening + reading + writing + speaking) / 4.0
        val integerPart = average.toInt()
        val fraction = average - integerPart

        return when {
            fraction < 0.25 -> integerPart.toDouble()
            fraction < 0.75 -> integerPart + 0.5
            else -> integerPart + 1.0
        }
    }
}
