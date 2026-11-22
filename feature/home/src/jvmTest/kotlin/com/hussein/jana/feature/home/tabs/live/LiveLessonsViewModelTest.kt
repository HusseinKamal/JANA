package com.hussein.jana.feature.home.tabs.live

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetNextLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetUpcomingLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.SyncCalendarUseCase
import com.hussein.jana.feature.home.tabs.live.presentation.list.LiveLessonsEffect
import com.hussein.jana.feature.home.tabs.live.presentation.list.LiveLessonsIntent
import com.hussein.jana.feature.home.tabs.live.presentation.list.LiveLessonsViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LiveLessonsViewModelTest {

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

    private val fakeRepo = object : LiveLessonRepository {
        override suspend fun getUpcomingLiveLessons(): Result<List<LiveLesson>> =
            Result.success(listOf(fakeLesson))

        override suspend fun getNextLiveLessons(): Result<List<LiveLesson>> =
            Result.success(listOf(fakeLesson.copy(id = "l2")))

        override suspend fun getLessonDetails(lessonId: String): Result<LiveLesson> =
            Result.success(fakeLesson)

        override suspend fun getLessonFiles(lessonId: String): Result<List<LessonFile>> =
            Result.success(emptyList())

        override suspend fun getLessonExams(lessonId: String): Result<List<LessonExam>> =
            Result.success(emptyList())

        override suspend fun syncCalendar(lessons: List<LiveLesson>): Result<Boolean> =
            Result.success(true)
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = LiveLessonsViewModel(
        getUpcomingLiveLessons = GetUpcomingLiveLessonsUseCase(fakeRepo),
        getNextLiveLessons = GetNextLiveLessonsUseCase(fakeRepo),
        syncCalendar = SyncCalendarUseCase(fakeRepo),
    )

    @Test
    fun `loadLessons success populates state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals(1, vm.state.value.upcomingLessons.size)
        assertEquals(1, vm.state.value.nextLessons.size)
    }

    @Test
    fun `JoinZoom intent emits OpenZoomUrl effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LiveLessonsIntent.JoinZoom("https://zoom.us/test"))

        val effect = vm.effect.first()
        assertTrue(effect is LiveLessonsEffect.OpenZoomUrl)
        assertEquals("https://zoom.us/test", effect.url)
    }

    @Test
    fun `OpenLessonDetail intent emits OpenLessonDetail effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LiveLessonsIntent.OpenLessonDetail("l1"))

        val effect = vm.effect.first()
        assertTrue(effect is LiveLessonsEffect.OpenLessonDetail)
        assertEquals("l1", effect.lessonId)
    }

    @Test
    fun `SyncCalendarClicked intent succeeds and updates state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onIntent(LiveLessonsIntent.SyncCalendarClicked)
        advanceUntilIdle()

        assertTrue(vm.state.value.isCalendarSynced)
    }
}
