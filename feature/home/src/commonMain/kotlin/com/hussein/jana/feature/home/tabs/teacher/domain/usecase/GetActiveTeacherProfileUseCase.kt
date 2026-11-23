package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherProfile
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherProfileRepository

class GetActiveTeacherProfileUseCase(private val repository: TeacherProfileRepository) {
    suspend operator fun invoke(): Result<TeacherProfile> = repository.getActiveTeacherProfile()
}
