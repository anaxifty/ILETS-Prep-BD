package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ielts_user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val PHONE = stringPreferencesKey("user_phone")
        val TARGET_BAND = doublePreferencesKey("target_band")
        val TEST_DATE = stringPreferencesKey("test_date")
        val LISTENING_BAND = doublePreferencesKey("listening_band")
        val READING_BAND = doublePreferencesKey("reading_band")
        val WRITING_BAND = doublePreferencesKey("writing_band")
        val SPEAKING_BAND = doublePreferencesKey("speaking_band")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            phone = prefs[PreferencesKeys.PHONE] ?: "",
            targetBand = prefs[PreferencesKeys.TARGET_BAND] ?: 7.0,
            testDate = prefs[PreferencesKeys.TEST_DATE] ?: "In 3 Months",
            listeningBand = prefs[PreferencesKeys.LISTENING_BAND] ?: 6.0,
            readingBand = prefs[PreferencesKeys.READING_BAND] ?: 6.0,
            writingBand = prefs[PreferencesKeys.WRITING_BAND] ?: 5.5,
            speakingBand = prefs[PreferencesKeys.SPEAKING_BAND] ?: 5.5,
            isOnboardingCompleted = prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PHONE] = profile.phone
            prefs[PreferencesKeys.TARGET_BAND] = profile.targetBand
            prefs[PreferencesKeys.TEST_DATE] = profile.testDate
            prefs[PreferencesKeys.LISTENING_BAND] = profile.listeningBand
            prefs[PreferencesKeys.READING_BAND] = profile.readingBand
            prefs[PreferencesKeys.WRITING_BAND] = profile.writingBand
            prefs[PreferencesKeys.SPEAKING_BAND] = profile.speakingBand
            prefs[PreferencesKeys.ONBOARDING_COMPLETED] = profile.isOnboardingCompleted
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
