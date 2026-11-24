package com.hussein.jana.feature.home.tabs.profile.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCourseUnitsUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.profile.domain.model.SyllabusCourse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Units and lessons of the courses the student is subscribed to. Units are fetched for all
 * courses concurrently; any failed call fails the whole result.
 */
class GetSyllabusUseCase(
    private val getCourses: GetCoursesUseCase,
    private val getCourseUnits: GetCourseUnitsUseCase,
) {
    suspend operator fun invoke(): Result<List<SyllabusCourse>> {
        val courses = getCourses().getOrElse { return Result.failure(it) }
            .filter { it.isSubscribed }
        val results = coroutineScope {
            courses.map { course ->
                async { getCourseUnits(course.id).map { units -> SyllabusCourse(course, units) } }
            }.awaitAll()
        }
        return runCatching { results.map { it.getOrThrow() } }
    }
}
