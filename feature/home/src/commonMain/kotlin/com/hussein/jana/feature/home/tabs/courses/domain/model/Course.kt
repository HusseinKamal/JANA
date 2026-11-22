package com.hussein.jana.feature.home.tabs.courses.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val totalLessons: Int,
    val completedLessons: Int,
    val subjects: List<String>,
)
