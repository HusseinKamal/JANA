package com.hussein.jana.feature.home.tabs.profile.data.repository

import com.hussein.jana.core.datastore.AppPreferences
import com.hussein.jana.core.datastore.StoredNotificationSettings
import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.core.designsystem.AppLocaleManager
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Language goes through [AppLocaleManager] (the app root re-provides the locale at runtime);
 * notification switches are persisted in DataStore via [AppPreferences].
 */
class AppSettingsRepositoryImpl(
    private val localeManager: AppLocaleManager,
    private val appPreferences: AppPreferences,
) : AppSettingsRepository {

    override fun observeLanguage(): Flow<AppLanguage> = localeManager.language

    override suspend fun setLanguage(language: AppLanguage) {
        localeManager.setLanguage(language)
    }

    override fun observeNotificationPreferences(): Flow<NotificationPreferences> =
        appPreferences.notificationSettings.map { it.toDomain() }

    // TODO: sync notification preferences with PUT /api/v1/me/notification-settings
    override suspend fun setNotificationPreferences(preferences: NotificationPreferences) {
        appPreferences.setNotificationSettings(preferences.toStored())
    }
}

private fun StoredNotificationSettings.toDomain() = NotificationPreferences(
    enabled = enabled,
    liveLessonReminders = liveLessonReminders,
    examReminders = examReminders,
    newMessages = newMessages,
    feedback = feedback,
    subscriptionExpiry = subscriptionExpiry,
)

private fun NotificationPreferences.toStored() = StoredNotificationSettings(
    enabled = enabled,
    liveLessonReminders = liveLessonReminders,
    examReminders = examReminders,
    newMessages = newMessages,
    feedback = feedback,
    subscriptionExpiry = subscriptionExpiry,
)
