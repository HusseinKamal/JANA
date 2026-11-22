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
    @Suppress("UNUSED_PARAMETER") private val api: LiveLessonApi = LiveLessonApi(),
) : LiveLessonRepository {

    // ---------------------------------------------------------------------------
    // Mock data — TODO: replace each method with real API call
    // ---------------------------------------------------------------------------

    // Static reference offset — represents "now" for all mock data.
    // Using a fixed base so the mock works consistently in commonMain
    // without depending on platform time APIs.
    private val baseMs = 1_700_000_000_000L  // roughly Nov 2023

    private val mockUpcoming = listOf(
        LiveLesson(
            id = "lesson_1",
            subject = "MATHEMATICS",
            level = "HIGH SCHOOL SENIOR",
            title = "Advanced Calculus & Integration Techniques",
            teacherName = "Dr. Ahmed Hassan",
            dateText = "Today, 4:00 PM",
            timeText = "4:00 PM – 5:30 PM",
            startEpochMs = baseMs + 60 * 60 * 1000L, // base + 1h
            status = LessonStatus.UPCOMING,
            minsUntilStart = 60,
            zoomUrl = "https://zoom.us/j/123456789",
            description = "Comprehensive deep-dive into calculus integration methods with live problem solving.",
        ),
    )

    private val mockLiveNow = listOf(
        LiveLesson(
            id = "lesson_live",
            subject = "PHYSICS",
            level = "HIGH SCHOOL JUNIOR",
            title = "Electromagnetism – Live Problem Session",
            teacherName = "Prof. Mona El-Sayed",
            dateText = "Now",
            timeText = "Live",
            startEpochMs = baseMs,
            status = LessonStatus.LIVE_NOW,
            minsUntilStart = 0,
            zoomUrl = "https://zoom.us/j/live_session",
            description = "Live problem solving on electromagnetic induction.",
        ),
    )

    private val mockNext = listOf(
        LiveLesson(
            id = "lesson_2",
            subject = "PHYSICS",
            level = "HIGH SCHOOL JUNIOR",
            title = "Quantum Mechanics & Wave Functions",
            teacherName = "Prof. Mona El-Sayed",
            dateText = "Tomorrow, 2:00 PM",
            timeText = "2:00 PM – 3:30 PM",
            startEpochMs = baseMs + 2 * 24 * 60 * 60 * 1000L, // base + 2d
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
            timeText = "5:00 PM – 6:30 PM",
            startEpochMs = baseMs + 5 * 24 * 60 * 60 * 1000L, // base + 5d
            status = LessonStatus.UPCOMING,
            minsUntilStart = 2880,
            zoomUrl = "https://zoom.us/j/456789123",
            description = "Detailed examination of organic reaction mechanisms.",
        ),
    )

    private val mockPrevious = listOf(
        LiveLesson(
            id = "lesson_prev_1",
            subject = "MATHEMATICS",
            level = "HIGH SCHOOL SENIOR",
            title = "Differential Equations – Session 3",
            teacherName = "Dr. Ahmed Hassan",
            dateText = "Mon, Nov 11, 4:00 PM",
            timeText = "4:00 PM – 5:30 PM",
            startEpochMs = baseMs - 3 * 24 * 60 * 60 * 1000L,
            status = LessonStatus.ENDED,
            minsUntilStart = 0,
            zoomUrl = "",
            description = "Review of differential equations with worked examples.",
        ),
        LiveLesson(
            id = "lesson_prev_2",
            subject = "PHYSICS",
            level = "HIGH SCHOOL JUNIOR",
            title = "Optics & Light Refraction",
            teacherName = "Prof. Mona El-Sayed",
            dateText = "Sat, Nov 9, 2:00 PM",
            timeText = "2:00 PM – 3:30 PM",
            startEpochMs = baseMs - 5 * 24 * 60 * 60 * 1000L,
            status = LessonStatus.ENDED,
            minsUntilStart = 0,
            zoomUrl = "",
            description = "Study of light behaviour through lenses and prisms.",
        ),
        LiveLesson(
            id = "lesson_prev_3",
            subject = "CHEMISTRY",
            level = "HIGH SCHOOL SENIOR",
            title = "Thermodynamics – Entropy & Enthalpy",
            teacherName = "Dr. Tarek Omar",
            dateText = "Thu, Nov 7, 5:00 PM",
            timeText = "5:00 PM – 6:30 PM",
            startEpochMs = baseMs - 7 * 24 * 60 * 60 * 1000L,
            status = LessonStatus.ENDED,
            minsUntilStart = 0,
            zoomUrl = "",
            description = "In-depth session on thermodynamic laws and energy calculations.",
        ),
    )

    private val mockFiles = listOf(
        LessonFile(
            id = "file_1",
            title = "Calculus Lecture Notes",
            sizeText = "2.4 MB",
            fileType = LessonFileType.PDF,
            downloadUrl = "https://example.com/files/calculus_notes.pdf",
        ),
        LessonFile(
            id = "file_2",
            title = "Integration Practice Slides",
            sizeText = "1.8 MB",
            fileType = LessonFileType.PPTX,
            downloadUrl = "https://example.com/files/integration_slides.pptx",
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

    // TODO: replace with real API call — GET /api/v1/live-lessons/upcoming
    override suspend fun getUpcomingLiveLessons(): Result<List<LiveLesson>> {
        delay(400)
        return Result.success(mockLiveNow + mockUpcoming)
    }

    // TODO: replace with real API call — GET /api/v1/live-lessons/next
    override suspend fun getNextLiveLessons(): Result<List<LiveLesson>> {
        delay(400)
        return Result.success(mockNext)
    }

    // TODO: replace with real API call — GET /api/v1/live-lessons/previous
    override suspend fun getPreviousLiveLessons(): Result<List<LiveLesson>> {
        delay(400)
        return Result.success(mockPrevious)
    }

    // TODO: replace with real API call — GET /api/v1/live-lessons/{id}
    override suspend fun getLessonDetails(lessonId: String): Result<LiveLesson> {
        delay(400)
        val all = mockLiveNow + mockUpcoming + mockNext + mockPrevious
        val lesson = all.find { it.id == lessonId } ?: mockUpcoming.first()
        return Result.success(lesson)
    }

    // TODO: replace with real API call — GET /api/v1/live-lessons/{id}/files
    override suspend fun getLessonFiles(lessonId: String): Result<List<LessonFile>> {
        delay(400)
        return Result.success(mockFiles)
    }

    // TODO: replace with real API call — GET /api/v1/live-lessons/{id}/exams
    override suspend fun getLessonExams(lessonId: String): Result<List<LessonExam>> {
        delay(400)
        return Result.success(mockExams)
    }

    override suspend fun syncCalendar(lessons: List<LiveLesson>): Result<Boolean> {
        val titles = lessons.map { it.title }
        val times = lessons.map { it.startEpochMs }
        val result = syncCalendar(titles, times)
        return Result.success(result)
    }
}
