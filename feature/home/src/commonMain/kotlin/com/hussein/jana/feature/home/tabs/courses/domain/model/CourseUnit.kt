package com.hussein.jana.feature.home.tabs.courses.domain.model

data class CourseUnit(
    val id: String,
    val title: String,
    val order: Int,
    val lessons: List<CourseLesson>,
)
