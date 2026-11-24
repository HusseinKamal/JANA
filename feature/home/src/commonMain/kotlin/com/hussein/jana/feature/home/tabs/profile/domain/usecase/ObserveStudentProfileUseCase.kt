package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.profile.domain.model.StudentProfile
import com.hussein.jana.feature.home.tabs.profile.domain.repository.StudentProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveStudentProfileUseCase(private val repository: StudentProfileRepository) {
    operator fun invoke(): Flow<StudentProfile> = repository.observeProfile()
}
