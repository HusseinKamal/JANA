package com.hussein.jana.feature.home.tabs.teacher.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository

/**
 * The active teacher's future lessons: live-now + upcoming + next, de-duplicated,
 * with LIVE_NOW lessons first and the rest ordered by start time.
 */
class GetTeacherUpcomingLessonsUseCase(private val repository: LiveLessonRepository) {
    suspend operator fun invoke(): Result<List<LiveLesson>> {
        val upcoming = repository.getUpcomingLiveLessons()
            .getOrElse { return Result.failure(it) }
        val next = repository.getNextLiveLessons()
            .getOrElse { return Result.failure(it) }
        val (liveNow, rest) = (upcoming + next)
            .distinctBy { it.id }
            .partition { it.status == LessonStatus.LIVE_NOW }
        return Result.success(liveNow + rest.sortedBy { it.startEpochMs })
    }
}
