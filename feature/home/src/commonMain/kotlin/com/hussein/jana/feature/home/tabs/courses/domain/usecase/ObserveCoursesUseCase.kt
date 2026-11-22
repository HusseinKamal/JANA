package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class ObserveCoursesUseCase(private val repository: CourseRepository) {
    operator fun invoke(): Flow<List<Course>> = repository.observeCourses()
}
