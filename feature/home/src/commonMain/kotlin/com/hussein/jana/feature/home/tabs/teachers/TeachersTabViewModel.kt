package com.hussein.jana.feature.home.tabs.teachers

import com.hussein.jana.core.common.mvi.BaseMviViewModel

/**
 * Localizable strings for the Teachers tab.
 *
 * Default values are plain English so the ViewModel works standalone in tests
 * without a Compose context. The screen calls [TeachersTabViewModel.updateStrings]
 * in a [androidx.compose.runtime.LaunchedEffect] with [org.jetbrains.compose.resources.stringResource]
 * resolved values so locale changes propagate without recreating the ViewModel.
 */
data class TeachersStrings(
    val profileComingSoon: String = "Teacher profile coming soon",
    val reserveComingSoon: String = "Reservation coming soon",
    val filtersComingSoon: String = "Advanced filters coming soon",
)

/**
 * ViewModel for the Teachers / Discover tab.
 *
 * Filtering is applied in-memory against [TeachersTabState.allTeachers].
 * Both search query and subject filter are independent axes that combine
 * with AND logic.
 */
class TeachersTabViewModel(
    private var strings: TeachersStrings = TeachersStrings(),
) : BaseMviViewModel<TeachersTabState, TeachersTabIntent, TeachersTabEffect>(TeachersTabState()) {

    /** Supply localized strings resolved in the composition layer. */
    fun updateStrings(s: TeachersStrings) {
        strings = s
    }

    override fun onIntent(intent: TeachersTabIntent) = when (intent) {
        is TeachersTabIntent.SearchQueryChanged ->
            applyFilters(query = intent.query)

        is TeachersTabIntent.FilterSelected ->
            applyFilters(filter = intent.filterKey)

        is TeachersTabIntent.ProfileClicked ->
            sendEffect(TeachersTabEffect.ShowSnackbar(strings.profileComingSoon))

        is TeachersTabIntent.ReserveClicked ->
            sendEffect(TeachersTabEffect.ShowSnackbar(strings.reserveComingSoon))

        TeachersTabIntent.FilterButtonClicked ->
            sendEffect(TeachersTabEffect.ShowSnackbar(strings.filtersComingSoon))
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun applyFilters(
        query: String = currentState().searchQuery,
        filter: String = currentState().selectedFilter,
    ) {
        val results = currentState().allTeachers.filter { teacher ->
            val matchesFilter = filter == "all" || teacher.subjectKey == filter
            val matchesSearch = query.isBlank() ||
                teacher.name.contains(query, ignoreCase = true) ||
                teacher.subjectDetail.contains(query, ignoreCase = true) ||
                teacher.subjectKey.contains(query, ignoreCase = true)
            matchesFilter && matchesSearch
        }
        setState { copy(searchQuery = query, selectedFilter = filter, filteredTeachers = results) }
    }
}
