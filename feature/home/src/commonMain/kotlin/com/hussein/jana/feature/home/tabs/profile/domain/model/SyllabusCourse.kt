package com.hussein.jana.feature.home.tabs.profile.domain.model

import com.hussein.jana.feature.home.tabs.courses.domain.model.Course
import com.hussein.jana.feature.home.tabs.courses.domain.model.CourseUnit

/** A subscribed course with its units and lessons. */
data class SyllabusCourse(
    val course: Course,
    val units: List<CourseUnit>,
)
