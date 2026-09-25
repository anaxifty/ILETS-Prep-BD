package com.example.data.models

data class UserProfile(
    val phone: String = "",
    val targetBand: Double = 7.0,
    val testDate: String = "In 3 Months",
    val listeningBand: Double = 6.0,
    val readingBand: Double = 6.0,
    val writingBand: Double = 5.5,
    val speakingBand: Double = 5.5,
    val isOnboardingCompleted: Boolean = false
) {
    val overallStartingBand: Double
        get() = ((listeningBand + readingBand + writingBand + speakingBand) / 4.0 * 2).toInt() / 2.0
}
