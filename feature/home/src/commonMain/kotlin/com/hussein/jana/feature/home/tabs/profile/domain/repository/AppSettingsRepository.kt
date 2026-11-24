package com.hussein.jana.feature.home.tabs.profile.domain.repository

import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

/** Device-level app settings: language and notification switches. */
interface AppSettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>

    suspend fun setLanguage(language: AppLanguage)

    fun observeNotificationPreferences(): Flow<NotificationPreferences>

    suspend fun setNotificationPreferences(preferences: NotificationPreferences)
}
