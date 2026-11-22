package com.hussein.jana.feature.home.tabs.live.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LiveLessonDto(
    val id: String,
    val subject: String,
    val level: String,
    val title: String,
    val teacherName: String,
    val dateText: String,
    val timeText: String,
    val startEpochMs: Long = 0L,
    val status: String = "UPCOMING",
    val minsUntilStart: Int = 0,
    val zoomUrl: String = "",
    val description: String = "",
)
