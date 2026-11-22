package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository

class GetCourseDetailUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(courseId: String): Result<Course> =
        repository.getCourseDetail(courseId)
}
