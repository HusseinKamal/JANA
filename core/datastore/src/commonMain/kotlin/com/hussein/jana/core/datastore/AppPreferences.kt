package com.hussein.jana.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Notification switches persisted on the device. Everything is on by default. */
data class StoredNotificationSettings(
    val enabled: Boolean = true,
    val liveLessonReminders: Boolean = true,
    val examReminders: Boolean = true,
    val newMessages: Boolean = true,
    val feedback: Boolean = true,
    val subscriptionExpiry: Boolean = true,
)

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")
    private val KEY_NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    private val KEY_NOTIF_LIVE_REMINDERS = booleanPreferencesKey("notif_live_reminders")
    private val KEY_NOTIF_EXAM_REMINDERS = booleanPreferencesKey("notif_exam_reminders")
    private val KEY_NOTIF_NEW_MESSAGES = booleanPreferencesKey("notif_new_messages")
    private val KEY_NOTIF_FEEDBACK = booleanPreferencesKey("notif_feedback")
    private val KEY_NOTIF_SUBSCRIPTION_EXPIRY = booleanPreferencesKey("notif_subscription_expiry")

    val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    val isOnboarded: Flow<Boolean> = dataStore.data.map { it[KEY_ONBOARDED] ?: false }

    val notificationSettings: Flow<StoredNotificationSettings> = dataStore.data.map { prefs ->
        val defaults = StoredNotificationSettings()
        StoredNotificationSettings(
            enabled = prefs[KEY_NOTIF_ENABLED] ?: defaults.enabled,
            liveLessonReminders = prefs[KEY_NOTIF_LIVE_REMINDERS] ?: defaults.liveLessonReminders,
            examReminders = prefs[KEY_NOTIF_EXAM_REMINDERS] ?: defaults.examReminders,
            newMessages = prefs[KEY_NOTIF_NEW_MESSAGES] ?: defaults.newMessages,
            feedback = prefs[KEY_NOTIF_FEEDBACK] ?: defaults.feedback,
            subscriptionExpiry = prefs[KEY_NOTIF_SUBSCRIPTION_EXPIRY] ?: defaults.subscriptionExpiry,
        )
    }

    suspend fun setNotificationSettings(settings: StoredNotificationSettings) {
        dataStore.edit {
            it[KEY_NOTIF_ENABLED] = settings.enabled
            it[KEY_NOTIF_LIVE_REMINDERS] = settings.liveLessonReminders
            it[KEY_NOTIF_EXAM_REMINDERS] = settings.examReminders
            it[KEY_NOTIF_NEW_MESSAGES] = settings.newMessages
            it[KEY_NOTIF_FEEDBACK] = settings.feedback
            it[KEY_NOTIF_SUBSCRIPTION_EXPIRY] = settings.subscriptionExpiry
        }
    }

    suspend fun setLanguage(code: String) {
        dataStore.edit { it[KEY_LANGUAGE] = code }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE] = isDark }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        dataStore.edit { it[KEY_ONBOARDED] = onboarded }
    }
}
