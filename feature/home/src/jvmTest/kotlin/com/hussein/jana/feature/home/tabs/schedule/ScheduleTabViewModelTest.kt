package com.hussein.jana.feature.home.tabs.schedule

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
import kotlin.test.assertTrue

/**
 * Unit tests for [ScheduleTabViewModel].
 *
 * Covers: initial state, day selection, subject filtering, combined filter,
 * one-shot effects for all intents, updateStrings behaviour, and correct
 * session-action snackbar messages.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleTabViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = ScheduleTabViewModel()

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial state has all mock sessions`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(mockSessions, vm.state.value.allSessions)
        assertEquals(mockSessions, vm.state.value.filteredSessions)
    }

    @Test
    fun `initial selectedFilter is all`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals("all", vm.state.value.selectedFilter)
    }

    @Test
    fun `initial selectedDate is 23`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(23, vm.state.value.selectedDate)
    }

    @Test
    fun `initial creditsAvailable is 12`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(12, vm.state.value.creditsAvailable)
    }

    @Test
    fun `initial filters list matches mockFilters`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(mockFilters, vm.state.value.filters)
    }

    // ── 2. DaySelected — only changes selectedDate, not filteredSessions ──────

    @Test
    fun `DaySelected changes selectedDate`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.DaySelected(25))
        advanceTimeBy(50)

        assertEquals(25, vm.state.value.selectedDate)
    }

    @Test
    fun `DaySelected does NOT change filteredSessions when filter is all`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val before = vm.state.value.filteredSessions

            vm.onIntent(ScheduleTabIntent.DaySelected(26))
            advanceTimeBy(50)

            // filteredSessions must be the same (day selection is not a filter axis)
            assertEquals(before, vm.state.value.filteredSessions)
        }

    // ── 3. FilterSelected — filters by subjectFilterKey ───────────────────────

    @Test
    fun `FilterSelected mathematics returns only math sessions`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("mathematics"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredSessions
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.subjectFilterKey == "mathematics" })
    }

    @Test
    fun `FilterSelected physics returns only physics sessions`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("physics"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredSessions
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.subjectFilterKey == "physics" })
    }

    @Test
    fun `FilterSelected chemistry returns only chemistry sessions`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("chemistry"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredSessions
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.subjectFilterKey == "chemistry" })
    }

    @Test
    fun `FilterSelected all after subject filter restores all sessions`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            vm.onIntent(ScheduleTabIntent.FilterSelected("mathematics"))
            advanceTimeBy(50)
            vm.onIntent(ScheduleTabIntent.FilterSelected("all"))
            advanceTimeBy(50)

            assertEquals(mockSessions.size, vm.state.value.filteredSessions.size)
        }

    @Test
    fun `FilterSelected unknown key returns empty list`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("biology"))
        advanceTimeBy(50)

        // None of the mock sessions have subjectFilterKey == "biology"
        assertTrue(vm.state.value.filteredSessions.isEmpty())
    }

    // ── 4. Combined day + subject filter ─────────────────────────────────────

    @Test
    fun `combined day and subject filter — day changes selectedDate, subject filters sessions`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            vm.onIntent(ScheduleTabIntent.FilterSelected("mathematics"))
            advanceTimeBy(50)
            vm.onIntent(ScheduleTabIntent.DaySelected(26))
            advanceTimeBy(50)

            // Date changed
            assertEquals(26, vm.state.value.selectedDate)
            // Session list is still filtered by subject only (mathematics), not by date
            assertTrue(vm.state.value.filteredSessions.all { it.subjectFilterKey == "mathematics" })
        }

    // ── 5. SyncCalendarClicked ────────────────────────────────────────────────

    @Test
    fun `SyncCalendarClicked emits ShowSnackbar with default syncComingSoon`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SyncCalendarClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(
                ScheduleStrings().syncComingSoon,
                (effect as ScheduleTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 6. TopUpClicked ───────────────────────────────────────────────────────

    @Test
    fun `TopUpClicked emits ShowSnackbar with default topUpComingSoon`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.TopUpClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(
                ScheduleStrings().topUpComingSoon,
                (effect as ScheduleTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 7. GoogleCalendarClicked ──────────────────────────────────────────────

    @Test
    fun `GoogleCalendarClicked emits ShowSnackbar with default googleComingSoon`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.GoogleCalendarClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(
                ScheduleStrings().googleComingSoon,
                (effect as ScheduleTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 8. AppleCalendarClicked ───────────────────────────────────────────────

    @Test
    fun `AppleCalendarClicked emits ShowSnackbar with default appleComingSoon`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.AppleCalendarClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(
                ScheduleStrings().appleComingSoon,
                (effect as ScheduleTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 9. SessionActionClicked — join ────────────────────────────────────────

    @Test
    fun `SessionActionClicked join emits joiningSession snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s1", "join"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().joiningSession, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 10. SessionActionClicked — prep ──────────────────────────────────────

    @Test
    fun `SessionActionClicked prep emits prepWithAi snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s1", "prep"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().prepWithAi, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 11. SessionActionClicked — notes ─────────────────────────────────────

    @Test
    fun `SessionActionClicked notes emits reviewNotes snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s2", "notes"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().reviewNotes, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 12. SessionActionClicked — socratic ──────────────────────────────────

    @Test
    fun `SessionActionClicked socratic emits aiSocratic snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s2", "socratic"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().aiSocratic, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 13. SessionActionClicked — reserve ───────────────────────────────────

    @Test
    fun `SessionActionClicked reserve emits reserveSession snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s4", "reserve"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().reserveSession, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 14. SessionActionClicked — book ──────────────────────────────────────

    @Test
    fun `SessionActionClicked book emits bookSeat snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s5", "book"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().bookSeat, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 15. SessionActionClicked — syllabus ───────────────────────────────────

    @Test
    fun `SessionActionClicked syllabus emits viewSyllabus snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s3", "syllabus"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().viewSyllabus, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 16. SessionActionClicked — unknown action ─────────────────────────────

    @Test
    fun `SessionActionClicked unknown action emits actionComingSoon snackbar`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s1", "unknown_xyz"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals(ScheduleStrings().actionComingSoon, (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 17. updateStrings changes snackbar messages ───────────────────────────

    @Test
    fun `updateStrings changes SyncCalendarClicked snackbar message`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val arabicStrings = ScheduleStrings(syncComingSoon = "مزامنة التقويم قريباً")
            vm.updateStrings(arabicStrings)

            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SyncCalendarClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals("مزامنة التقويم قريباً", (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    @Test
    fun `updateStrings changes SessionActionClicked join snackbar message`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val arabicStrings = ScheduleStrings(joiningSession = "جارٍ الانضمام...")
            vm.updateStrings(arabicStrings)

            val effects = mutableListOf<ScheduleTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(ScheduleTabIntent.SessionActionClicked("s1", "join"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is ScheduleTabEffect.ShowSnackbar)
            assertEquals("جارٍ الانضمام...", (effect as ScheduleTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 18. FilterSelected updates selectedFilter in state ────────────────────

    @Test
    fun `FilterSelected updates selectedFilter in state`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("physics"))
        advanceTimeBy(50)

        assertEquals("physics", vm.state.value.selectedFilter)
    }

    // ── 19. DaySelected keeps existing subject filter ─────────────────────────

    @Test
    fun `DaySelected preserves existing selectedFilter`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(ScheduleTabIntent.FilterSelected("chemistry"))
        advanceTimeBy(50)
        vm.onIntent(ScheduleTabIntent.DaySelected(27))
        advanceTimeBy(50)

        assertEquals("chemistry", vm.state.value.selectedFilter)
        assertEquals(27, vm.state.value.selectedDate)
    }

    // ── 20. allSessions never modified by intents ─────────────────────────────

    @Test
    fun `allSessions is never modified by filter or day intents`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            vm.onIntent(ScheduleTabIntent.FilterSelected("mathematics"))
            advanceTimeBy(50)
            vm.onIntent(ScheduleTabIntent.DaySelected(24))
            advanceTimeBy(50)

            // allSessions is the full source-of-truth and must never shrink
            assertEquals(mockSessions, vm.state.value.allSessions)
        }
}
