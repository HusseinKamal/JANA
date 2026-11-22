package com.hussein.jana.feature.home.tabs.live.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository

class GetLessonDetailsUseCase(
    private val repository: LiveLessonRepository,
) {
    suspend operator fun invoke(lessonId: String): Result<LiveLesson> =
        repository.getLessonDetails(lessonId)
}
