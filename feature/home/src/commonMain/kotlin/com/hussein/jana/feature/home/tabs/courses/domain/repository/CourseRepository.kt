package com.hussein.jana.feature.home.tabs.courses.domain.repository

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun getCourseDetail(courseId: String): Result<Course>
    suspend fun getCourseUnits(courseId: String): Result<List<CourseUnit>>
    fun observeCourses(): Flow<List<Course>>
}
