package com.example.data.models

enum class ListeningQuestionType {
    MULTIPLE_CHOICE,
    FORM_COMPLETION,
    MATCHING
}

data class ListeningQuestion(
    val id: Int = 0,
    val sectionNumber: Int = 1,
    val type: ListeningQuestionType = ListeningQuestionType.MULTIPLE_CHOICE,
    val questionText: String = "",
    val formContext: String = "",
    val options: List<String> = emptyList(),
    val matchingOptions: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = ""
)

data class ListeningTest(
    val id: String = "",
    val title: String = "",
    val difficultyLevel: String = "Medium",
    val targetBand: Double = 6.5,
    val audioUrl: String = "",
    val audioDurationSeconds: Int = 180,
    val transferTimeSeconds: Int = 600,
    val questions: List<ListeningQuestion> = emptyList()
)

data class ListeningAttempt(
    val id: String = "",
    val testId: String = "",
    val testTitle: String = "",
    val userId: String = "",
    val userAnswers: Map<String, String> = emptyMap(),
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val bandScore: Double = 0.0,
    val timeTakenSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
