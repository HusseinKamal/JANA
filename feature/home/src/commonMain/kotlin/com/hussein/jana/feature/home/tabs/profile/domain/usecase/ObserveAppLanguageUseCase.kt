package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveAppLanguageUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(): Flow<AppLanguage> = repository.observeLanguage()
}
