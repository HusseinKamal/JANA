package com.hussein.jana.feature.home.tabs.live.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonExamDto(
    val id: String,
    val title: String,
    val isTimed: Boolean,
    val durationMinutes: Int = 0,
    val examUrl: String = "",
)
