package com.hussein.jana.feature.home.tabs.schedule

import com.hussein.jana.core.common.mvi.BaseMviViewModel

/**
 * Localizable strings for the Schedule tab.
 *
 * Default values are plain English so the ViewModel works standalone in tests
 * without a Compose context. The screen calls [ScheduleTabViewModel.updateStrings]
 * in a [androidx.compose.runtime.LaunchedEffect] with
 * [org.jetbrains.compose.resources.stringResource]-resolved values so locale
 * changes propagate without recreating the ViewModel.
 */
data class ScheduleStrings(
    val syncComingSoon: String = "Calendar sync coming soon",
    val topUpComingSoon: String = "Top up coming soon",
    val googleComingSoon: String = "Google Calendar integration coming soon",
    val appleComingSoon: String = "Apple iCal integration coming soon",
    val joiningSession: String = "Joining session...",
    val prepWithAi: String = "Opening AI prep session...",
    val reviewNotes: String = "Opening review notes...",
    val aiSocratic: String = "Opening AI Socratic Primer...",
    val reserveSession: String = "Reserving session...",
    val bookSeat: String = "Booking seat...",
    val viewSyllabus: String = "Opening syllabus...",
    val actionComingSoon: String = "Coming soon",
)

/**
 * ViewModel for the Schedule tab.
 *
 * Day selection updates only [ScheduleTabState.selectedDate] — it does NOT
 * filter the session list. Only subject-filter selection changes
 * [ScheduleTabState.filteredSessions].
 */
class ScheduleTabViewModel(
    private var strings: ScheduleStrings = ScheduleStrings(),
) : BaseMviViewModel<ScheduleTabState, ScheduleTabIntent, ScheduleTabEffect>(ScheduleTabState()) {

    /** Supply localized strings resolved in the composition layer. */
    fun updateStrings(s: ScheduleStrings) {
        strings = s
    }

    override fun onIntent(intent: ScheduleTabIntent) = when (intent) {
        is ScheduleTabIntent.DaySelected ->
            applyDayFilter(intent.date)

        is ScheduleTabIntent.FilterSelected ->
            applySubjectFilter(intent.key)

        is ScheduleTabIntent.SessionActionClicked ->
            handleSessionAction(intent.sessionId, intent.actionId)

        ScheduleTabIntent.SyncCalendarClicked ->
            sendEffect(ScheduleTabEffect.OpenCalendar)

        ScheduleTabIntent.TopUpClicked ->
            sendEffect(ScheduleTabEffect.ShowSnackbar(strings.topUpComingSoon))

        ScheduleTabIntent.GoogleCalendarClicked ->
            sendEffect(ScheduleTabEffect.ShowSnackbar(strings.googleComingSoon))

        ScheduleTabIntent.AppleCalendarClicked ->
            sendEffect(ScheduleTabEffect.ShowSnackbar(strings.appleComingSoon))
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Day selection changes [ScheduleTabState.selectedDate] only.
     * The session list is re-filtered by subject to keep both axes consistent,
     * but date is intentionally not used as a filter axis.
     */
    private fun applyDayFilter(date: Int) {
        setState {
            copy(
                selectedDate = date,
                filteredSessions = filterSessions(date, selectedFilter),
            )
        }
    }

    private fun applySubjectFilter(key: String) {
        setState {
            copy(
                selectedFilter = key,
                filteredSessions = filterSessions(selectedDate, key),
            )
        }
    }

    /**
     * Filters sessions by subject key only.
     *
     * The [date] parameter is accepted to allow future date-based filtering,
     * but is intentionally unused today — this keeps day selection a pure
     * UI-state concern without changing the visible session list.
     */
    private fun filterSessions(date: Int, subjectKey: String): List<SessionSlot> {
        @Suppress("UNUSED_PARAMETER")
        return currentState().allSessions.filter { session ->
            val matchesSubject = subjectKey == "all" || session.subjectFilterKey == subjectKey
            matchesSubject
        }
    }

    private fun handleSessionAction(sessionId: String, actionId: String) {
        val msg = when (actionId) {
            "join"     -> strings.joiningSession
            "prep"     -> strings.prepWithAi
            "notes"    -> strings.reviewNotes
            "socratic" -> strings.aiSocratic
            "reserve"  -> strings.reserveSession
            "book"     -> strings.bookSeat
            "syllabus" -> strings.viewSyllabus
            else       -> strings.actionComingSoon
        }
        sendEffect(ScheduleTabEffect.ShowSnackbar(msg))
    }
}
