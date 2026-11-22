package com.hussein.jana.feature.home.tabs.courses.domain.usecase

import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit
import com.hussein.jana.feature.home.tabs.courses.domain.repository.CourseRepository

class GetCourseUnitsUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(courseId: String): Result<List<CourseUnit>> =
        repository.getCourseUnits(courseId)
}
