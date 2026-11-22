package com.hussein.jana.feature.home.tabs.live.domain.repository

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson

interface LiveLessonRepository {
    suspend fun getUpcomingLiveLessons(): Result<List<LiveLesson>>
    suspend fun getNextLiveLessons(): Result<List<LiveLesson>>
    suspend fun getPreviousLiveLessons(): Result<List<LiveLesson>>
    suspend fun getLessonDetails(lessonId: String): Result<LiveLesson>
    suspend fun getLessonFiles(lessonId: String): Result<List<LessonFile>>
    suspend fun getLessonExams(lessonId: String): Result<List<LessonExam>>
    suspend fun syncCalendar(lessons: List<LiveLesson>): Result<Boolean>
}
