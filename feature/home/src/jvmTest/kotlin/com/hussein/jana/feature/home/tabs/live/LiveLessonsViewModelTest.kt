package com.hussein.jana.feature.home.tabs.live

import com.hussein.jana.feature.home.tabs.live.domain.model.LessonExam
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonFile
import com.hussein.jana.feature.home.tabs.live.domain.model.LessonStatus
import com.hussein.jana.feature.home.tabs.live.domain.model.LiveLesson
import com.hussein.jana.feature.home.tabs.live.domain.repository.LiveLessonRepository
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetNextLiveLessonsUseCase
import com.hussein.jana.feature.home.tabs.live.domain.usecase.GetPreviousLiveLessonsUseCase
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
import kotlin.test.assertNotNull
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
        startEpochMs = 1_700_000_000_000L,
        zoomUrl = "https://zoom.us/test",
    )

    // Finding 2 fix: fakeRepo now implements all repository methods including getPreviousLiveLessons
    private val fakeRepo = object : LiveLessonRepository {
        override suspend fun getUpcomingLiveLessons(): Result<List<LiveLesson>> =
            Result.success(listOf(fakeLesson))

        override suspend fun getNextLiveLessons(): Result<List<LiveLesson>> =
            Result.success(listOf(fakeLesson.copy(id = "l2")))

        override suspend fun getPreviousLiveLessons(): Result<List<LiveLesson>> =
            Result.success(listOf(fakeLesson.copy(id = "l3", status = LessonStatus.ENDED)))

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

    // Finding 2 fix: constructor now takes 4 params (added getPreviousLiveLessons)
    private fun buildViewModel() = LiveLessonsViewModel(
        getUpcomingLiveLessons = GetUpcomingLiveLessonsUseCase(fakeRepo),
        getNextLiveLessons = GetNextLiveLessonsUseCase(fakeRepo),
        getPreviousLiveLessons = GetPreviousLiveLessonsUseCase(fakeRepo),
        syncCalendar = SyncCalendarUseCase(fakeRepo),
    )

    @Test
    fun `loadLessons success populates state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        // Finding 2 fix: upcomingLessons list field removed; check upcomingLesson (nullable hero)
        assertNotNull(vm.state.value.upcomingLesson)
        assertEquals("l1", vm.state.value.upcomingLesson?.id)
        assertEquals(1, vm.state.value.nextLessons.size)
        assertEquals(1, vm.state.value.previousLessons.size)
    }

    @Test
    fun `JoinZoom intent with url emits OpenZoomUrl effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // Finding 2 fix: JoinZoom now takes (lessonId, url) — two arguments
        vm.onIntent(LiveLessonsIntent.JoinZoom("l1", "https://zoom.us/test"))

        val effect = vm.effect.first()
        assertTrue(effect is LiveLessonsEffect.OpenZoomUrl)
        assertEquals("https://zoom.us/test", effect.url)
    }

    @Test
    fun `JoinZoom intent with blank url emits ShowNoZoomLink effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        // Finding 1 fix: no longer emits ShowSnackbar — emits typed ShowNoZoomLink
        vm.onIntent(LiveLessonsIntent.JoinZoom("l1", ""))

        val effect = vm.effect.first()
        assertTrue(effect is LiveLessonsEffect.ShowNoZoomLink)
    }

    @Test
    fun `OpenLessonDetail intent emits NavigateToDetail effect`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(LiveLessonsIntent.OpenLessonDetail("l1"))

        // Finding 2 fix: effect renamed from OpenLessonDetail to NavigateToDetail
        val effect = vm.effect.first()
        assertTrue(effect is LiveLessonsEffect.NavigateToDetail)
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
