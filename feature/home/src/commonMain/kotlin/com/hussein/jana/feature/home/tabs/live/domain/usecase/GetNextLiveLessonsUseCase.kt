package com.hussein.jana.feature.home.tabs.live.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository

class GetNextLiveLessonsUseCase(
    private val repository: LiveLessonRepository,
) {
    suspend operator fun invoke(): Result<List<LiveLesson>> =
        repository.getNextLiveLessons()
}
