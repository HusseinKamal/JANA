package com.hussein.jana.feature.home.tabs.courses.data.repository

import com.hussein.jana.feature.home.tabs.courses.data.local.CourseCatalogStore
import com.hussein.jana.feature.home.tabs.courses.data.mock.MockCourseData
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class CourseRepositoryImpl(
    private val store: CourseCatalogStore,
) : CourseRepository {

    // TODO: replace with real API call — GET /api/v1/courses
    //  (scoped to the active teacher platform via the authenticated session)
    override suspend fun getCourses(): Result<List<Course>> {
        delay(400)
        return Result.success(store.courses.value)
    }

    // TODO: replace with real API call — GET /api/v1/courses/{id}
    //  (scoped to the active teacher platform via the authenticated session)
    override suspend fun getCourseDetail(courseId: String): Result<Course> {
        delay(400)
        val course = store.find(courseId)
            ?: return Result.failure(NoSuchElementException("Course $courseId not found"))
        return Result.success(course)
    }

    // TODO: replace with real API call — GET /api/v1/courses/{id}/units
    //  (scoped to the active teacher platform via the authenticated session)
    override suspend fun getCourseUnits(courseId: String): Result<List<CourseUnit>> {
        delay(400)
        val course = store.find(courseId)
        val completed = if (course?.isSubscribed == true) course.completedLessons else 0
        return Result.success(MockCourseData.mockUnitsFor(courseId, completedLessons = completed))
    }

    override fun observeCourses(): Flow<List<Course>> = store.courses
}
