package com.hussein.jana.feature.home.tabs.live.domain.model

data class LessonExam(
    val id: String,
    val title: String,
    val isTimed: Boolean,
    val durationMinutes: Int = 0,
    val examUrl: String = "",
)
