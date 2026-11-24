package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.model.NotificationPreferences
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveNotificationPreferencesUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(): Flow<NotificationPreferences> = repository.observeNotificationPreferences()
}
