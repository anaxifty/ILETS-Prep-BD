package com.example.data.models

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE_NOT_GIVEN,
    FILL_IN_BLANK
}

data class ReadingQuestion(
    val id: Int = 0,
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val questionText: String = "",
    val options: List<String> = emptyList(), // For MULTIPLE_CHOICE e.g. ["A", "B", "C", "D"] or option texts
    val correctAnswer: String = "",
    val explanation: String = ""
)

data class ReadingTest(
    val id: String = "",
    val title: String = "",
    val difficultyLevel: String = "Medium", // Easy, Medium, Hard
    val targetBand: Double = 6.5,
    val timeLimitMinutes: Int = 60,
    val passageText: String = "",
    val questions: List<ReadingQuestion> = emptyList()
)

data class ReadingAttempt(
    val id: String = "",
    val testId: String = "",
    val testTitle: String = "",
    val userId: String = "",
    val userAnswers: Map<String, String> = emptyMap(), // Question ID string -> User answer
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val bandScore: Double = 0.0,
    val timeTakenSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
