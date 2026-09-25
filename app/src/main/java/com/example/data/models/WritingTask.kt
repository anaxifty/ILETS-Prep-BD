package com.example.data.models

enum class WritingTaskType {
    TASK_1, // Chart, graph, diagram, process description (Target: 150 words, 20 mins)
    TASK_2  // Discursive / opinion / problem-solution essay (Target: 250 words, 40 mins)
}

data class WritingTask(
    val id: String = "",
    val taskType: String = "Task 1", // "Task 1" or "Task 2"
    val title: String = "",
    val prompt: String = "",
    val imageUrl: String? = null,
    val difficultyLevel: String = "Medium", // Easy, Medium, Hard
    val targetWordCount: Int = 150,
    val recommendedTimeMinutes: Int = 20
)

data class WritingCriterionScore(
    val criterionName: String = "",
    val score: Double = 0.0,
    val feedbackNote: String = ""
)

data class WritingAttempt(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val taskType: String = "Task 1",
    val userId: String = "",
    val inputMethod: String = "TEXT", // "TEXT" or "PHOTO_SCAN"
    val answerText: String = "",
    val photoUri: String? = null,
    val overallBand: Double = 0.0,
    val criteriaScores: List<WritingCriterionScore> = emptyList(),
    val generalFeedback: String = "",
    val wordCount: Int = 0,
    val timeTakenSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
