package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.core.designsystem.AppLanguage
import com.hussein.jana.feature.home.tabs.profile.domain.repository.AppSettingsRepository

class SetAppLanguageUseCase(private val repository: AppSettingsRepository) {
    suspend operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}
