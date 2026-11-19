package com.hussein.jana.feature.home.tabs.schedule

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.core.designsystem.WeekDay

// ── Status / style enums ──────────────────────────────────────────────────────

enum class SessionStatus {
    TODAY,           // green "in X mins" badge
    TOMORROW,        // "Reserved Seat" amber
    RESERVED,        // "Reserved Seat" amber
    RECOMMENDED,     // "Recommended For You" teal
    OPEN_ENROLLMENT, // "Open Enrollment" gray
}

enum class SessionActionStyle { PRIMARY_TEAL, OUTLINE, AMBER }

// ── Data models ───────────────────────────────────────────────────────────────

data class SessionAction(
    val id: String,
    val labelKey: String,   // string-resource key resolved in the UI layer
    val style: SessionActionStyle,
)

data class ScheduleFilter(
    val key: String,
    val label: String,
    val count: Int,
)

data class SessionSlot(
    val id: String,
    /** Groups sessions under a header: "today", "tomorrow", "thursday", "saturday", "sunday". */
    val dayGroupKey: String,
    /** Human-readable day label, e.g. "Wednesday, 23 Oct". */
    val dayDisplayLabel: String,
    val time: String,
    /** Short subject label shown in the top badge, e.g. "Math • Algebra". */
    val subject: String,
    val title: String,
    val teacher: String,
    val durationMins: Int,
    /** Session format or room label, e.g. "Zoom • Room #204". */
    val formatLabel: String,
    val status: SessionStatus,
    val seatsLabel: String? = null,
    val aiNote: String? = null,
    val prerequisiteNote: String? = null,
    val hasPdf: Boolean = false,
    val calendarSynced: Boolean = false,
    /** Minutes until session starts; meaningful only for [SessionStatus.TODAY]. */
    val minutesUntil: Int = 0,
    val pricingNote: String? = null,
    val pairingNote: String? = null,
    val actions: List<SessionAction>,
    /** Key used for subject-based filtering, e.g. "mathematics", "physics". */
    val subjectFilterKey: String,
)

// ── Mock data ─────────────────────────────────────────────────────────────────

val mockWeekDays: List<WeekDay> = listOf(
    WeekDay("Mon", 21, false),
    WeekDay("Tue", 22, false),
    WeekDay("Wed", 23, true),
    WeekDay("Thu", 24, true),
    WeekDay("Fri", 25, false),
    WeekDay("Sat", 26, true),
    WeekDay("Sun", 27, true),
)

val mockFilters: List<ScheduleFilter> = listOf(
    ScheduleFilter("all", "All", 0),
    ScheduleFilter("mathematics", "Mathematics", 2),
    ScheduleFilter("physics", "Physics", 2),
    ScheduleFilter("chemistry", "Chemistry", 1),
)

val mockSessions: List<SessionSlot> = listOf(
    SessionSlot(
        id = "s1",
        dayGroupKey = "today",
        dayDisplayLabel = "Wednesday, 23 Oct",
        time = "18:00",
        subject = "Math • Algebra",
        title = "Algebra – Quadratic Equations & Factoring",
        teacher = "Dr. Ahmed Hassan",
        durationMins = 60,
        formatLabel = "Zoom • Room #204",
        status = SessionStatus.TODAY,
        minutesUntil = 42,
        aiNote = "AI Companion: Formulas flashcard ready (4 cards)",
        actions = listOf(
            SessionAction("prep", "schedule_prep_ai", SessionActionStyle.OUTLINE),
            SessionAction("join", "schedule_join_live", SessionActionStyle.PRIMARY_TEAL),
        ),
        subjectFilterKey = "mathematics",
    ),
    SessionSlot(
        id = "s2",
        dayGroupKey = "tomorrow",
        dayDisplayLabel = "Tomorrow, 24 Oct",
        time = "17:00",
        subject = "Geometry",
        title = "Geometry – Analytical Trigonometry & Vectors",
        teacher = "Dr. Mona El-Sayed",
        durationMins = 75,
        formatLabel = "Whiteboard-feedback",
        status = SessionStatus.TOMORROW,
        seatsLabel = "M / 20 Seats Filled",
        prerequisiteNote = "Prerequisite: Unit circle radians review",
        hasPdf = true,
        actions = listOf(
            SessionAction("syllabus", "schedule_view_syllabus", SessionActionStyle.OUTLINE),
        ),
        subjectFilterKey = "mathematics",
    ),
    // s3 — Thursday
    SessionSlot(
        id = "s3",
        dayGroupKey = "thursday",
        dayDisplayLabel = "Thursday, 24 Oct",
        time = "19:30",
        subject = "Physics • Mechanics Block II",
        title = "Classical Mechanics: Inclined Planes & Friction",
        teacher = "Eng. Tarek Nour",
        durationMins = 90,
        formatLabel = "Live Problem Set",
        status = SessionStatus.RESERVED,
        calendarSynced = true,
        actions = listOf(
            SessionAction("syllabus", "schedule_view_syllabus", SessionActionStyle.OUTLINE),
        ),
        subjectFilterKey = "physics",
    ),
    SessionSlot(
        id = "s4",
        dayGroupKey = "saturday",
        dayDisplayLabel = "Saturday, 26 Oct",
        time = "11:00 AM",
        subject = "Calculus",
        title = "Calculus – Limits, Continuity & Epsilon-Delta",
        teacher = "Dr. Ahmed Hassan",
        durationMins = 75,
        formatLabel = "1 Credit",
        status = SessionStatus.RECOMMENDED,
        seatsLabel = "Only 2 Seats Open",
        pricingNote = "1 Credit",
        pairingNote = "Pairs with your completed analysis module",
        actions = listOf(
            SessionAction("reserve", "schedule_reserve_credit", SessionActionStyle.PRIMARY_TEAL),
        ),
        subjectFilterKey = "mathematics",
    ),
    SessionSlot(
        id = "s5",
        dayGroupKey = "sunday",
        dayDisplayLabel = "Sunday, 27 Oct",
        time = "16:30",
        subject = "Chemistry • Organic Chemistry I",
        title = "Hydrocarbons & Reaction Mechanisms",
        teacher = "Prof. Laila Farouk",
        durationMins = 60,
        formatLabel = "Interactive Lab",
        status = SessionStatus.OPEN_ENROLLMENT,
        aiNote = "JAs: 8 students booked",
        actions = listOf(
            SessionAction("book", "schedule_book_seat", SessionActionStyle.OUTLINE),
        ),
        subjectFilterKey = "chemistry",
    ),
)

// ── MVI State ─────────────────────────────────────────────────────────────────

data class ScheduleTabState(
    val selectedDate: Int = 23,
    val weekDays: List<WeekDay> = mockWeekDays,
    val selectedFilter: String = "all",
    val filters: List<ScheduleFilter> = mockFilters,
    val allSessions: List<SessionSlot> = mockSessions,
    val filteredSessions: List<SessionSlot> = mockSessions,
    val creditsAvailable: Int = 12,
    val creditsReserved: Int = 1,
    val currentMonth: String = "October 2024",
    val location: String = "Cairo (GMT+3)",
) : MviState

// ── MVI Intent ────────────────────────────────────────────────────────────────

sealed interface ScheduleTabIntent : MviIntent {
    data class DaySelected(val date: Int) : ScheduleTabIntent
    data class FilterSelected(val key: String) : ScheduleTabIntent
    data class SessionActionClicked(val sessionId: String, val actionId: String) : ScheduleTabIntent
    data object SyncCalendarClicked : ScheduleTabIntent
    data object TopUpClicked : ScheduleTabIntent
    data object GoogleCalendarClicked : ScheduleTabIntent
    data object AppleCalendarClicked : ScheduleTabIntent
}

// ── MVI Effect ────────────────────────────────────────────────────────────────

sealed interface ScheduleTabEffect : MviEffect {
    data class ShowSnackbar(val message: String) : ScheduleTabEffect
    /** Opens the device-native calendar app (Android/iOS/Desktop). */
    data object OpenCalendar : ScheduleTabEffect
}
