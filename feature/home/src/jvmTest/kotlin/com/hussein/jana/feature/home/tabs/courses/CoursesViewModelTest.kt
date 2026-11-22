package com.hussein.jana.feature.home.tabs.courses

import com.hussein.jana.feature.home.tabs.courses.domain.usecase.GetCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.domain.usecase.ObserveCoursesUseCase
import com.hussein.jana.feature.home.tabs.courses.presentation.list.CoursesEffect
import com.hussein.jana.feature.home.tabs.courses.presentation.list.CoursesIntent
import com.hussein.jana.feature.home.tabs.courses.presentation.list.CoursesViewModel
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
class CoursesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCourseRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCourseRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = CoursesViewModel(
        getCourses = GetCoursesUseCase(repository),
        observeCourses = ObserveCoursesUseCase(repository),
    )

    @Test
    fun `load maps subscribed and locked courses`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.courses.size)
        val subscribed = state.courses.first { it.id == subscribedCourse.id }
        val locked = state.courses.first { it.id == lockedCourse.id }
        assertTrue(subscribed.isSubscribed)
        assertEquals(4, subscribed.completedLessons)
        assertFalse(locked.isSubscribed)
        assertEquals("EGP", locked.price?.currencyCode)
    }

    @Test
    fun `OpenCourse navigates without checkout`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onIntent(CoursesIntent.OpenCourse(subscribedCourse.id))

        assertEquals(CoursesEffect.NavigateToCourseDetail(subscribedCourse.id), vm.effect.first())
    }

    @Test
    fun `SubscribeClicked navigates to details with checkout open`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onIntent(CoursesIntent.SubscribeClicked(lockedCourse.id))

        assertEquals(
            CoursesEffect.NavigateToCourseDetail(lockedCourse.id, openCheckout = true),
            vm.effect.first(),
        )
    }

    @Test
    fun `list refreshes when a verified subscription updates the catalogue`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertFalse(vm.state.value.courses.first { it.id == lockedCourse.id }.isSubscribed)

        repository.markSubscribed(lockedCourse.id)
        advanceUntilIdle()

        assertTrue(vm.state.value.courses.first { it.id == lockedCourse.id }.isSubscribed)
    }
}
