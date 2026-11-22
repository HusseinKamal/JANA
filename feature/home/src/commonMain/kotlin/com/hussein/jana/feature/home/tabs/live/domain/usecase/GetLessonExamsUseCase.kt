package com.hussein.jana.feature.home.tabs.live.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository

class GetLessonExamsUseCase(
    private val repository: LiveLessonRepository,
) {
    suspend operator fun invoke(lessonId: String): Result<List<LessonExam>> =
        repository.getLessonExams(lessonId)
}
