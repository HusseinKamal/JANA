package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository

class UpdateStudentProfileUseCase(private val repository: StudentProfileRepository) {
    suspend operator fun invoke(fullName: String, email: String, phone: String): Result<StudentProfile> =
        repository.updateProfile(fullName, email, phone)
}
