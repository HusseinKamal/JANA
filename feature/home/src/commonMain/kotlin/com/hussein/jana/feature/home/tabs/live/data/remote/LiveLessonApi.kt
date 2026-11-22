package com.hussein.jana.feature.home.tabs.live.data.remote

import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LessonExamDto
import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LessonFileDto
import com.hussein.jana.feature.home.tabs.live.data.remote.dto.LiveLessonDto

class LiveLessonApi {
    // TODO: replace with real API call
    suspend fun getUpcomingLessons(): List<LiveLessonDto> = emptyList()

    // TODO: replace with real API call
    suspend fun getNextLessons(): List<LiveLessonDto> = emptyList()

    // TODO: replace with real API call
    suspend fun getLessonFiles(lessonId: String): List<LessonFileDto> = emptyList()

    // TODO: replace with real API call
    suspend fun getLessonExams(lessonId: String): List<LessonExamDto> = emptyList()
}
