package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository

class UpdateNotificationPreferencesUseCase(private val repository: AppSettingsRepository) {
    suspend operator fun invoke(preferences: NotificationPreferences) =
        repository.setNotificationPreferences(preferences)
}
