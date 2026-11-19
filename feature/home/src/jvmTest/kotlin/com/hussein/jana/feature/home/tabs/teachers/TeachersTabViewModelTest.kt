package com.hussein.jana.feature.home.tabs.teachers

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
 * Unit tests for [TeachersTabViewModel].
 *
 * Covers: initial state, search/filter MVI intents, combined filters,
 * empty results, case-insensitivity, effects, updateStrings, and formatK.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TeachersTabViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = TeachersTabViewModel()

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial state has all mock teachers`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(mockTeachers.size, vm.state.value.filteredTeachers.size)
        assertEquals(mockTeachers, vm.state.value.allTeachers)
    }

    @Test
    fun `initial selectedFilter is all`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals("all", vm.state.value.selectedFilter)
    }

    @Test
    fun `initial searchQuery is empty`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals("", vm.state.value.searchQuery)
    }

    @Test
    fun `initial filters list matches defaultFilters`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(defaultFilters, vm.state.value.filters)
    }

    // ── 2. SearchQueryChanged filters by name ─────────────────────────────────

    @Test
    fun `SearchQueryChanged filters by teacher name`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("Ahmed"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredTeachers
        assertEquals(1, results.size)
        assertEquals("Dr. Ahmed Hassan", results.first().name)
    }

    @Test
    fun `SearchQueryChanged filters by subject detail`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("Algebra"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredTeachers
        // "Algebra, Geometry, Calculus" is in Dr. Ahmed Hassan
        assertTrue(results.any { it.name == "Dr. Ahmed Hassan" })
    }

    // ── 3. FilterSelected filters by subject key ──────────────────────────────

    @Test
    fun `FilterSelected mathematics returns only math teachers`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.FilterSelected("mathematics"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredTeachers
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.subjectKey == "mathematics" })
    }

    @Test
    fun `FilterSelected physics returns only physics teachers`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.FilterSelected("physics"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredTeachers
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.subjectKey == "physics" })
    }

    // ── 4. Combined search + filter ───────────────────────────────────────────

    @Test
    fun `combined filter and search narrows results correctly`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.FilterSelected("mathematics"))
        advanceTimeBy(50)
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("Ahmed"))
        advanceTimeBy(50)

        val results = vm.state.value.filteredTeachers
        assertEquals(1, results.size)
        assertEquals("Dr. Ahmed Hassan", results.first().name)
        // Filter should still be mathematics
        assertEquals("mathematics", vm.state.value.selectedFilter)
    }

    // ── 5. Empty results when no match ────────────────────────────────────────

    @Test
    fun `SearchQueryChanged with no match returns empty list`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("zzz_no_match_xyz"))
        advanceTimeBy(50)

        assertTrue(vm.state.value.filteredTeachers.isEmpty())
    }

    @Test
    fun `FilterSelected with no teachers in subject returns empty list`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.FilterSelected("biology"))
        advanceTimeBy(50)

        // None of the mock teachers teach biology
        assertTrue(vm.state.value.filteredTeachers.isEmpty())
    }

    // ── 6. Case-insensitive search ────────────────────────────────────────────

    @Test
    fun `search is case-insensitive for teacher names`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("ahmed"))   // lowercase
        advanceTimeBy(50)

        assertTrue(vm.state.value.filteredTeachers.any { it.name == "Dr. Ahmed Hassan" })
    }

    @Test
    fun `search is case-insensitive for subject key`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("CHEMISTRY"))
        advanceTimeBy(50)

        assertTrue(vm.state.value.filteredTeachers.any { it.subjectKey == "chemistry" })
    }

    // ── 7. Blank query restores list ──────────────────────────────────────────

    @Test
    fun `blank query after search restores all teachers`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("Ahmed"))
        advanceTimeBy(50)
        vm.onIntent(TeachersTabIntent.SearchQueryChanged(""))
        advanceTimeBy(50)

        assertEquals(mockTeachers.size, vm.state.value.filteredTeachers.size)
    }

    // ── 8. ProfileClicked emits ShowSnackbar ──────────────────────────────────

    @Test
    fun `ProfileClicked emits ShowSnackbar with default profile string`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<TeachersTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(TeachersTabIntent.ProfileClicked("1"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is TeachersTabEffect.ShowSnackbar)
            assertEquals(
                TeachersStrings().profileComingSoon,
                (effect as TeachersTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 9. ReserveClicked emits ShowSnackbar ──────────────────────────────────

    @Test
    fun `ReserveClicked emits ShowSnackbar with default reserve string`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<TeachersTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(TeachersTabIntent.ReserveClicked("2"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is TeachersTabEffect.ShowSnackbar)
            assertEquals(
                TeachersStrings().reserveComingSoon,
                (effect as TeachersTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 10. FilterButtonClicked emits ShowSnackbar ────────────────────────────

    @Test
    fun `FilterButtonClicked emits ShowSnackbar with default filters string`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val effects = mutableListOf<TeachersTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(TeachersTabIntent.FilterButtonClicked)
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is TeachersTabEffect.ShowSnackbar)
            assertEquals(
                TeachersStrings().filtersComingSoon,
                (effect as TeachersTabEffect.ShowSnackbar).message,
            )
            collector.cancel()
        }

    // ── 11. updateStrings changes the snackbar message ────────────────────────

    @Test
    fun `updateStrings changes ProfileClicked snackbar message`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            val customStrings = TeachersStrings(
                profileComingSoon = "Profil bald verfügbar",
                reserveComingSoon = "Reservierung bald",
                filtersComingSoon = "Filter bald",
            )
            vm.updateStrings(customStrings)

            val effects = mutableListOf<TeachersTabEffect>()
            val collector = launch { vm.effect.collect { effects.add(it) } }

            vm.onIntent(TeachersTabIntent.ProfileClicked("1"))
            advanceTimeBy(100)

            val effect = effects.firstOrNull()
            assertTrue(effect is TeachersTabEffect.ShowSnackbar)
            assertEquals("Profil bald verfügbar", (effect as TeachersTabEffect.ShowSnackbar).message)
            collector.cancel()
        }

    // ── 12. Changing filter keeps the existing query ──────────────────────────

    @Test
    fun `FilterSelected preserves existing searchQuery in state`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(TeachersTabIntent.SearchQueryChanged("Dr"))
        advanceTimeBy(50)
        vm.onIntent(TeachersTabIntent.FilterSelected("mathematics"))
        advanceTimeBy(50)

        assertEquals("Dr", vm.state.value.searchQuery)
        assertEquals("mathematics", vm.state.value.selectedFilter)
    }

    @Test
    fun `SearchQueryChanged preserves existing selectedFilter in state`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            vm.onIntent(TeachersTabIntent.FilterSelected("physics"))
            advanceTimeBy(50)
            vm.onIntent(TeachersTabIntent.SearchQueryChanged("Mona"))
            advanceTimeBy(50)

            assertEquals("physics", vm.state.value.selectedFilter)
            assertEquals("Mona", vm.state.value.searchQuery)
        }

    // ── 13. Selecting 'all' after a filter restores full list ─────────────────

    @Test
    fun `FilterSelected all after subject filter restores all teachers`() =
        runTest(testDispatcher) {
            val vm = buildVm()
            vm.onIntent(TeachersTabIntent.FilterSelected("mathematics"))
            advanceTimeBy(50)
            vm.onIntent(TeachersTabIntent.FilterSelected("all"))
            advanceTimeBy(50)

            assertEquals(mockTeachers.size, vm.state.value.filteredTeachers.size)
        }

    // ── 14 & 15. formatK extension ─────────────────────────────────────────────

    @Test
    fun `formatK formats 1250 as 1 point 2k`() {
        assertEquals("1.2k", 1250.formatK())
    }

    @Test
    fun `formatK formats 820 as plain string`() {
        assertEquals("820", 820.formatK())
    }

    @Test
    fun `formatK formats exactly 1000 as 1 point 0k`() {
        assertEquals("1.0k", 1000.formatK())
    }

    @Test
    fun `formatK formats 999 as plain string`() {
        assertEquals("999", 999.formatK())
    }
}
