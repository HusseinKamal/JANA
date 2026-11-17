package com.hussein.jana.feature.home.tabs.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [HomeTabViewModel]:
 *  - time-of-day greeting selection
 *  - each intent emits the correct [HomeTabEffect.ShowSnackbar] carrying
 *    the expected resource key string
 *
 * [StringResource.key] is used for assertions instead of importing the
 * generated extension properties, which are not on the jvmTest classpath.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeTabViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ── Greeting time-of-day ───────────────────────────────────────────────────

    @Test
    fun `hour 0 sets morning greeting`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 0)
        assertEquals("home_greeting_morning", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 7 sets morning greeting`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 7)
        assertEquals("home_greeting_morning", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 11 sets morning greeting (upper boundary)`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 11)
        assertEquals("home_greeting_morning", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 12 sets afternoon greeting (lower boundary)`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 12)
        assertEquals("home_greeting_afternoon", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 15 sets afternoon greeting`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 15)
        assertEquals("home_greeting_afternoon", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 17 sets afternoon greeting (upper boundary)`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 17)
        assertEquals("home_greeting_afternoon", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 18 sets evening greeting (lower boundary)`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 18)
        assertEquals("home_greeting_evening", vm.state.value.greetingRes.key)
    }

    @Test
    fun `hour 23 sets evening greeting`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 23)
        assertEquals("home_greeting_evening", vm.state.value.greetingRes.key)
    }

    // ── Initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial state is not loading`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 8)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `initial state has three subjects`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 8)
        assertEquals(3, vm.state.value.subjects.size)
    }

    @Test
    fun `initial state has two upcoming sessions`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 8)
        assertEquals(2, vm.state.value.upcomingSessions.size)
    }

    @Test
    fun `initial state has ai highlight phrases`() = runTest(testDispatcher) {
        val vm = HomeTabViewModel(currentHour = 8)
        assertTrue(vm.state.value.aiHighlightPhrases.isNotEmpty())
    }

    // ── Intent → ShowSnackbar effects ─────────────────────────────────────────

    @Test
    fun `JoinSessionClicked emits ShowSnackbar with home_snack_joining`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.JoinSessionClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_joining", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `QuickQuizClicked emits ShowSnackbar with home_snack_quiz`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.QuickQuizClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_quiz", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `FormulaSheetClicked emits ShowSnackbar with home_snack_formula`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.FormulaSheetClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_formula", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `BreakdownClicked emits ShowSnackbar with home_snack_breakdown`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.BreakdownClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_breakdown", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `ViewCalendarClicked emits ShowSnackbar with home_snack_calendar`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.ViewCalendarClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_calendar", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `RefillPlanClicked emits ShowSnackbar with home_snack_refill`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.RefillPlanClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_refill", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }

    @Test
    fun `NotificationClicked emits ShowSnackbar with home_snack_notifications`() =
        runTest(testDispatcher) {
            val vm = HomeTabViewModel(currentHour = 8)
            val effects = mutableListOf<HomeTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(HomeTabIntent.NotificationClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is HomeTabEffect.ShowSnackbar)
            assertEquals("home_snack_notifications", (effect as HomeTabEffect.ShowSnackbar).messageRes.key)
            collector.cancel()
        }
}
