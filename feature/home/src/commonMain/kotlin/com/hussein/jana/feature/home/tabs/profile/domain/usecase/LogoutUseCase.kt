package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository

class LogoutUseCase(private val repository: StudentProfileRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
