package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository

class GetStudentProfileUseCase(private val repository: StudentProfileRepository) {
    suspend operator fun invoke(): Result<StudentProfile> = repository.getProfile()
}
