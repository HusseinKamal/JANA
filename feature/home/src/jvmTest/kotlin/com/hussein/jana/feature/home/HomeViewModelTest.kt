package com.hussein.jana.feature.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [HomeViewModel] — tab selection intent handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = HomeViewModel()

    // ── Initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial state has LIVE_LESSONS tab selected`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(HomeTab.LIVE_LESSONS, vm.state.value.selectedTab)
    }

    @Test
    fun `initial state has correct userName`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals("Hussein", vm.state.value.userName)
    }

    @Test
    fun `initial state has credits`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        assertEquals(12, vm.state.value.credits)
    }

    // ── Tab selection ──────────────────────────────────────────────────────────

    @Test
    fun `TabSelected MY_TEACHER_CHAT updates selectedTab`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(HomeIntent.TabSelected(HomeTab.MY_TEACHER_CHAT))
        assertEquals(HomeTab.MY_TEACHER_CHAT, vm.state.value.selectedTab)
    }

    @Test
    fun `TabSelected JANA_AI updates selectedTab`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(HomeIntent.TabSelected(HomeTab.JANA_AI))
        assertEquals(HomeTab.JANA_AI, vm.state.value.selectedTab)
    }

    @Test
    fun `TabSelected COURSES updates selectedTab`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(HomeIntent.TabSelected(HomeTab.COURSES))
        assertEquals(HomeTab.COURSES, vm.state.value.selectedTab)
    }

    @Test
    fun `TabSelected PROFILE updates selectedTab`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(HomeIntent.TabSelected(HomeTab.PROFILE))
        assertEquals(HomeTab.PROFILE, vm.state.value.selectedTab)
    }

    @Test
    fun `TabSelected LIVE_LESSONS returns to LIVE_LESSONS tab`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        vm.onIntent(HomeIntent.TabSelected(HomeTab.MY_TEACHER_CHAT))
        vm.onIntent(HomeIntent.TabSelected(HomeTab.LIVE_LESSONS))
        assertEquals(HomeTab.LIVE_LESSONS, vm.state.value.selectedTab)
    }
}
