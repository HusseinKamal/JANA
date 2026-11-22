package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository

class GetCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): Result<List<Course>> = repository.getCourses()
}
