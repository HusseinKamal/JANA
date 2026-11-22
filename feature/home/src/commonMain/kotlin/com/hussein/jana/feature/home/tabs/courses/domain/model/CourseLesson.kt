package com.hussein.jana.feature.home.tabs.courses.domain.model

data class CourseLesson(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val hasExam: Boolean,
)
