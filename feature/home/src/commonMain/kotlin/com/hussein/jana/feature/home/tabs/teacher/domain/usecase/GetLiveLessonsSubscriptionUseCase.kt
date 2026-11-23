package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.teacher.domain.model.TeacherSubscription
import com.hussein.jana.feature.home.tabs.teacher.domain.repository.TeacherSubscriptionRepository

class GetLiveLessonsSubscriptionUseCase(private val repository: TeacherSubscriptionRepository) {
    suspend operator fun invoke(): Result<TeacherSubscription> = repository.getLiveLessonsSubscription()
}
