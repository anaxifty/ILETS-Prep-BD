package com.example.data.models

data class SpeakingTask(
    val id: String = "",
    val partNumber: Int = 1, // 1 = Part 1, 2 = Part 2 Cue Card, 3 = Part 3 Discussion, or 0 = Full Test
    val title: String = "",
    val topic: String = "",
    val part1Questions: List<String> = emptyList(),
    val part2CueCard: String = "",
    val part2Bullets: List<String> = emptyList(),
    val part3Questions: List<String> = emptyList(),
    val difficultyLevel: String = "Medium"
)

data class SpeakingCriterionScore(
    val criterionName: String = "",
    val score: Double = 0.0,
    val feedbackNote: String = ""
)

data class SpeakingAttempt(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val partNumber: Int = 1,
    val userId: String = "",
    val audioFilePath: String? = null,
    val transcription: String = "",
    val overallBand: Double = 0.0,
    val criteriaScores: List<SpeakingCriterionScore> = emptyList(),
    val generalFeedback: String = "",
    val pronunciationAssessmentNote: String = "",
    val instructorReview: String? = null, // Placeholder for human review as requested
    val timestamp: Long = System.currentTimeMillis()
)
