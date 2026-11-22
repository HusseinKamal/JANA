package com.hussein.jana.feature.home.tabs.live

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFileType
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonDetailsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonExamsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetLessonFilesUseCase
import com.hussein.jana.feature.home.tabs.live.presentation.detail.LessonDetailEffect
import com.hussein.jana.feature.home.tabs.live.presentation.detail.LessonDetailIntent
import com.hussein.jana.feature.home.tabs.live.presentation.detail.LessonDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LessonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeLesson = LiveLesson(
        id = "l1",
        subject = "Math",
        level = "Senior",
        title = "Calculus",
        teacherName = "Dr. Ahmed",
        dateText = "Today",
        timeText = "4:00 PM",
        status = LessonStatus.UPCOMING,
    )

    private val fakeFile = LessonFile(
        id = "f1",
        title = "Notes",
        sizeText = "1MB",
        fileType = LessonFileType.PDF,
        downloadUrl = "https://example.com/notes.pdf",
    )

    private val fakeExam = LessonExam(
        id = "e1",
        title = "Midterm",
        isTimed = true,
        durationMinutes = 30,
    )

    private val fakeRepo = object : LiveLessonRepository {
        override suspend fun getUpcomingLiveLessons() = Result.success(emptyList<LiveLesson>())
        override suspend fun getNextLiveLessons() = Result.success(emptyList<LiveLesson>())
        override suspend fun getPreviousLiveLessons() = Result.success(emptyList<LiveLesson>())
        override suspend fun getLessonDetails(lessonId: String) = Result.success(fakeLesson)
        override suspend fun getLessonFiles(lessonId: String) = Result.success(listOf(fakeFile))
        override suspend fun getLessonExams(lessonId: String) = Result.success(listOf(fakeExam))
        override suspend fun syncCalendar(lessons: List<LiveLesson>) = Result.success(true)
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = LessonDetailViewModel(
        lessonId = "l1",
        getLessonDetails = GetLessonDetailsUseCase(fakeRepo),
        getLessonFiles = GetLessonFilesUseCase(fakeRepo),
        getLessonExams = GetLessonExamsUseCase(fakeRepo),
    )

    @Test
    fun `loadDetail success populates state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertNotNull(vm.state.value.lesson)
        assertEquals(1, vm.state.value.files.size)
        assertEquals(1, vm.state.value.exams.size)
    }

    @Test
    fun `PreviewFileClicked intent maps PDF to application-pdf mimeType`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LessonDetailIntent.PreviewFileClicked(fakeFile))

        val effect = vm.effect.first()
        assertTrue(effect is LessonDetailEffect.OpenFilePreview)
        assertEquals("application/pdf", (effect as LessonDetailEffect.OpenFilePreview).mimeType)
    }

    @Test
    fun `DownloadFileClicked intent emits DownloadFile effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LessonDetailIntent.DownloadFileClicked(fakeFile))

        val effect = vm.effect.first()
        assertTrue(effect is LessonDetailEffect.DownloadFile)
        assertEquals("https://example.com/notes.pdf", (effect as LessonDetailEffect.DownloadFile).url)
    }
}
