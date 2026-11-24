package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetPreviousLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.model.CourseProgress
import com.hussein.jana.feature.home.tabs.profile.domain.model.LearningProgress
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Builds the student's learning summary from the subscribed courses' syllabus and the live
 * lessons history.
 *
 * - Lessons: per course from [Course.completedLessons] / [Course.totalLessons].
 * - Exams: completed lessons that have an exam (no per-student exam results exist yet).
 * - Attended live lessons: number of previous live lessons, or null when that call fails.
 *
 * TODO: replace with GET /api/v1/me/progress once the backend exposes real progress data.
 */
class GetLearningProgressUseCase(
    private val getSyllabus: GetSyllabusUseCase,
    private val getPreviousLiveLessons: GetPreviousLiveLessonsUseCase,
) {
    suspend operator fun invoke(): Result<LearningProgress> = coroutineScope {
        val previousLessons = async { getPreviousLiveLessons() }
        val syllabus = getSyllabus().getOrElse { return@coroutineScope Result.failure(it) }

        val courses = syllabus.map { item ->
            CourseProgress(
                courseId = item.course.id,
                title = item.course.title,
                completedLessons = item.course.completedLessons,
                totalLessons = item.course.totalLessons,
            )
        }
        val examsCompleted = syllabus.sumOf { item ->
            item.units.sumOf { unit -> unit.lessons.count { it.isCompleted && it.hasExam } }
        }

        Result.success(
            LearningProgress(
                courses = courses,
                completedLessons = courses.sumOf { it.completedLessons },
                totalLessons = courses.sumOf { it.totalLessons },
                attendedLiveLessons = previousLessons.await().getOrNull()?.size,
                examsCompleted = examsCompleted,
            ),
        )
    }
}
