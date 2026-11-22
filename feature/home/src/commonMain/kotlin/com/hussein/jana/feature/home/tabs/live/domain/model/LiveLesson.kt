package com.hussein.jana.feature.home.tabs.live.domain.model

enum class LessonStatus {
    UPCOMING, LIVE_NOW, ENDED
}

data class LiveLesson(
    val id: String,
    val subject: String,
    val level: String,
    val title: String,
    val teacherName: String,
    val dateText: String,
    val timeText: String,
    val startEpochMs: Long = 0L,
    val status: LessonStatus = LessonStatus.UPCOMING,
    val minsUntilStart: Int = 0,
    val zoomUrl: String = "",
    val description: String = "",
)
