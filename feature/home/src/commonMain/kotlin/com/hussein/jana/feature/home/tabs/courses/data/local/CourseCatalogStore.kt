package com.hussein.jana.feature.home.tabs.courses.data.local

import com.hussein.jana.feature.home.tabs.courses.data.mock.MockCourseData
import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single in-memory source of truth for the course catalogue, shared by
 * [com.hussein.jana.feature.home.tabs.courses.data.repository.CourseRepositoryImpl] and
 * [com.hussein.jana.feature.home.tabs.courses.data.repository.CoursePaymentRepositoryImpl]
 * so the Courses list and Course Details always agree.
 *
 * Today it acts as the mock backend. Once the real API exists it becomes the cache
 * filled by API responses (course list, course detail, payment verification).
 */
class CourseCatalogStore(initial: List<Course> = MockCourseData.courses) {

    private val _courses = MutableStateFlow(initial)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    fun find(courseId: String): Course? = _courses.value.find { it.id == courseId }

    /** Called only with a backend-verified payment result. */
    fun markSubscribed(courseId: String) {
        _courses.update { list ->
            list.map { if (it.id == courseId) it.copy(isSubscribed = true) else it }
        }
    }
}
