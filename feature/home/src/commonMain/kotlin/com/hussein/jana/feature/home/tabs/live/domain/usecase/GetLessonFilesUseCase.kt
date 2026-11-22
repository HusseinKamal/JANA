package com.hussein.jana.feature.home.tabs.live.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository

class GetLessonFilesUseCase(
    private val repository: LiveLessonRepository,
) {
    suspend operator fun invoke(lessonId: String): Result<List<LessonFile>> =
        repository.getLessonFiles(lessonId)
}
