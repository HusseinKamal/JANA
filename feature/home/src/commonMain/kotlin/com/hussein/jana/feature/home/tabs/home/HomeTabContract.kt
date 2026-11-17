package com.hussein.jana.feature.home.tabs.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

// ── Data models ───────────────────────────────────────────────────────────────

data class LiveSessionData(
    val subject: String = "MATHEMATICS",
    val level: String = "HIGH SCHOOL SENIOR",
    val courseTitle: String = "Algebra – Quadratic Equations & Factoring",
    val teacherName: String = "Dr. Ahmed Hassan",
    val teacherRating: Float = 4.9f,
    val teacherReviews: Int = 142,
    val timeRange: String = "18:00 – 18:45",
    val minsUntilStart: Int = 42,
    val enrolledCount: Int = 18,
    val totalSeats: Int = 20,
    val isToday: Boolean = true,
)

/** @property nameKey  Display name of the subject (e.g. "Algebra")
 *  @property progress 0..1 mastery fraction
 *  @property levelKey Key used to look up localized chip label: "Proficient" | "Developing" | "Foundations"
 *  @property trend    Short trend blurb shown below the name */
data class SubjectMastery(
    val nameKey: String,
    val progress: Float,
    val levelKey: String,
    val trend: String = "",
)

data class UpcomingSessionData(
    val dayLabel: String,
    val timeLabel: String,
    val courseTitle: String,
    val teacherName: String,
    val isReserved: Boolean = true,
)

// ── MVI contract ──────────────────────────────────────────────────────────────

data class HomeTabState(
    val userName: String = "Hussein",
    val credits: Int = 12,
    val liveSession: LiveSessionData = LiveSessionData(),
    val aiMessage: String = "Want to prepare for today's algebra session? " +
        "Dr. Hassan usually begins with discriminant formulas and polynomial factoring.",
    val subjects: List<SubjectMastery> = listOf(
        SubjectMastery("Algebra", 0.72f, "Proficient", "+12% this week"),
        SubjectMastery("Geometry", 0.54f, "Developing", "Analytical proofs pending"),
        SubjectMastery("Physics", 0.45f, "Foundations", "Kinematics completed"),
    ),
    val upcomingSessions: List<UpcomingSessionData> = listOf(
        UpcomingSessionData("Tomorrow", "17:00", "Geometry – Analytical Trigonometry", "Dr. Mona El-Sayed"),
        UpcomingSessionData("Thursday", "19:30", "Physics – Classical Mechanics & Friction", "Eng. Tarek Nour"),
    ),
    val isLoading: Boolean = false,
) : MviState

sealed interface HomeTabIntent : MviIntent {
    data object JoinSessionClicked : HomeTabIntent
    data object QuickQuizClicked : HomeTabIntent
    data object FormulaSheetClicked : HomeTabIntent
    data object BreakdownClicked : HomeTabIntent
    data object ViewCalendarClicked : HomeTabIntent
    data object RefillPlanClicked : HomeTabIntent
    data object NotificationClicked : HomeTabIntent
}

sealed interface HomeTabEffect : MviEffect {
    data class ShowSnackbar(val message: String) : HomeTabEffect
}
