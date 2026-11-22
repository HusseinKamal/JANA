package com.hussein.jana.feature.home.tabs.live.data.repository

import com.hussein.jana.core.designsystem.syncCalendar
import com.hussein.jana.feature.home.tabs.live.data.remote.LiveLessonApi
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFileType
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository
import kotlinx.coroutines.delay

class LiveLessonRepositoryImpl(
    private val api: LiveLessonApi = LiveLessonApi(),
) : LiveLessonRepository {

    // Mock data
    private val mockUpcoming = listOf(
        LiveLesson(
            id = "lesson_1",
            subject = "MATHEMATICS",
            level = "HIGH SCHOOL SENIOR",
            title = "Advanced Calculus & Integration Techniques",
            teacherName = "Dr. Ahmed Hassan",
            dateText = "Today, 4:00 PM",
            timeText = "4:00 PM - 5:30 PM",
            startEpochMs = 1700000000000L,
            status = LessonStatus.UPCOMING,
            minsUntilStart = 15,
            zoomUrl = "https://zoom.us/j/123456789",
            description = "Comprehensive deep-dive into calculus integration methods with live problem solving.",
        )
    )

    private val mockNext = listOf(
        LiveLesson(
            id = "lesson_2",
            subject = "PHYSICS",
            level = "HIGH SCHOOL JUNIOR",
            title = "Quantum Mechanics & Wave Functions",
            teacherName = "Prof. Mona El-Sayed",
            dateText = "Tomorrow, 2:00 PM",
            timeText = "2:00 PM - 3:30 PM",
            startEpochMs = 1700086400000L,
            status = LessonStatus.UPCOMING,
            minsUntilStart = 1440,
            zoomUrl = "https://zoom.us/j/987654321",
            description = "Explore principles of quantum physics and wave mechanics.",
        ),
        LiveLesson(
            id = "lesson_3",
            subject = "CHEMISTRY",
            level = "HIGH SCHOOL SENIOR",
            title = "Organic Chemistry Reactions & Mechanisms",
            teacherName = "Dr. Tarek Omar",
            dateText = "Thu, Nov 16, 5:00 PM",
            timeText = "5:00 PM - 6:30 PM",
            startEpochMs = 1700172800000L,
            status = LessonStatus.UPCOMING,
            minsUntilStart = 2880,
            zoomUrl = "https://zoom.us/j/456789123",
            description = "Detailed examination of organic reaction mechanisms.",
        ),
    )

    private val mockFiles = listOf(
        LessonFile(
            id = "file_1",
            title = "Calculus Lecture Notes (PDF)",
            sizeText = "2.4 MB",
            fileType = LessonFileType.PDF,
            downloadUrl = "https://example.com/files/calculus_notes.pdf",
        ),
        LessonFile(
            id = "file_2",
            title = "Integration Practice Problems (DOCX)",
            sizeText = "1.1 MB",
            fileType = LessonFileType.DOCX,
            downloadUrl = "https://example.com/files/practice_problems.docx",
        ),
    )

    private val mockExams = listOf(
        LessonExam(
            id = "exam_1",
            title = "Calculus Mid-Term Assessment",
            isTimed = true,
            durationMinutes = 45,
            examUrl = "https://example.com/exams/calculus_midterm",
        ),
    )

    // TODO: replace with real API call
    override suspend fun getUpcomingLiveLessons(): Result<List<LiveLesson>> {
        delay(300)
        return Result.success(mockUpcoming)
    }

    // TODO: replace with real API call
    override suspend fun getNextLiveLessons(): Result<List<LiveLesson>> {
        delay(300)
        return Result.success(mockNext)
    }

    // TODO: replace with real API call
    override suspend fun getLessonDetails(lessonId: String): Result<LiveLesson> {
        delay(300)
        val lesson = (mockUpcoming + mockNext).find { it.id == lessonId }
            ?: mockUpcoming.first()
        return Result.success(lesson)
    }

    // TODO: replace with real API call
    override suspend fun getLessonFiles(lessonId: String): Result<List<LessonFile>> {
        delay(300)
        return Result.success(mockFiles)
    }

    // TODO: replace with real API call
    override suspend fun getLessonExams(lessonId: String): Result<List<LessonExam>> {
        delay(300)
        return Result.success(mockExams)
    }

    override suspend fun syncCalendar(lessons: List<LiveLesson>): Result<Boolean> {
        val titles = lessons.map { it.title }
        val times = lessons.map { it.startEpochMs }
        val result = syncCalendar(titles, times)
        return Result.success(result)
    }
}
