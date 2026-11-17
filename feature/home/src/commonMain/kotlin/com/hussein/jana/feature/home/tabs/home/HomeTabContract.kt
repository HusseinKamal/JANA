package com.hussein.jana.feature.home.tabs.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.home_ai_message_default
import com.hussein.jana.core.designsystem.home_ai_phrase_discriminant
import com.hussein.jana.core.designsystem.home_ai_phrase_polynomial
import com.hussein.jana.core.designsystem.home_day_thursday
import com.hussein.jana.core.designsystem.home_day_tomorrow
import com.hussein.jana.core.designsystem.home_greeting_morning
import com.hussein.jana.core.designsystem.home_live_level_senior
import com.hussein.jana.core.designsystem.home_live_subject_math
import com.hussein.jana.core.designsystem.home_subject_algebra
import com.hussein.jana.core.designsystem.home_subject_geometry
import com.hussein.jana.core.designsystem.home_subject_physics
import com.hussein.jana.core.designsystem.home_trend_algebra
import com.hussein.jana.core.designsystem.home_trend_geometry
import com.hussein.jana.core.designsystem.home_trend_physics
import org.jetbrains.compose.resources.StringResource

// ── Data models ───────────────────────────────────────────────────────────────

data class LiveSessionData(
    /** Localised subject label (e.g. Res.string.home_live_subject_math). */
    val subjectRes: StringResource = Res.string.home_live_subject_math,
    /** Localised level label (e.g. Res.string.home_live_level_senior). */
    val levelRes: StringResource = Res.string.home_live_level_senior,
    /** Course title — content data, not a UI label; supplied by the data layer. */
    val courseTitle: String = "Algebra – Quadratic Equations & Factoring",
    /** Teacher name — content data, not a UI label; supplied by the data layer. */
    val teacherName: String = "Dr. Ahmed Hassan",
    val teacherRating: Float = 4.9f,
    val teacherReviews: Int = 142,
    val timeRange: String = "18:00 – 18:45",
    val minsUntilStart: Int = 42,
    val enrolledCount: Int = 18,
    val totalSeats: Int = 20,
    val isToday: Boolean = true,
)

/** Subject mastery entry with fully localised display strings.
 *
 *  @property nameRes   StringResource for the subject name (e.g. home_subject_algebra).
 *  @property progress  Mastery fraction in [0..1].
 *  @property levelKey  Chip key — one of "Proficient", "Developing", "Foundations" — mapped
 *                      to a localised string in [SubjectProgressRow].
 *  @property trendRes  Optional StringResource for the trend blurb (may be null for no trend). */
data class SubjectMastery(
    val nameRes: StringResource,
    val progress: Float,
    val levelKey: String,
    val trendRes: StringResource? = null,
)

data class UpcomingSessionData(
    /** Localised day label (e.g. Res.string.home_day_tomorrow). */
    val dayLabelRes: StringResource,
    /** Time string (e.g. "17:00"). Not a UI label; not localised. */
    val timeLabel: String,
    /** Course title — content data; supplied by the data layer. */
    val courseTitle: String,
    /** Teacher name — content data; supplied by the data layer. */
    val teacherName: String,
    val isReserved: Boolean = true,
)

// ── MVI contract ──────────────────────────────────────────────────────────────

data class HomeTabState(
    val userName: String = "Hussein",
    /** Localised greeting resource (morning / afternoon / evening), computed in the ViewModel
     *  from the wall clock so it can vary by locale AND time of day. */
    val greetingRes: StringResource = Res.string.home_greeting_morning,
    val liveSession: LiveSessionData = LiveSessionData(),
    /** Localised AI companion message resource. */
    val aiMessageRes: StringResource = Res.string.home_ai_message_default,
    /** Localised phrases to highlight in the AI message.
     *  Stored as StringResource so they remain in sync with [aiMessageRes] when locale changes. */
    val aiHighlightPhrases: List<StringResource> = listOf(
        Res.string.home_ai_phrase_discriminant,
        Res.string.home_ai_phrase_polynomial,
    ),
    val subjects: List<SubjectMastery> = listOf(
        SubjectMastery(Res.string.home_subject_algebra, 0.72f, "Proficient", Res.string.home_trend_algebra),
        SubjectMastery(Res.string.home_subject_geometry, 0.54f, "Developing", Res.string.home_trend_geometry),
        SubjectMastery(Res.string.home_subject_physics, 0.45f, "Foundations", Res.string.home_trend_physics),
    ),
    val upcomingSessions: List<UpcomingSessionData> = listOf(
        UpcomingSessionData(Res.string.home_day_tomorrow, "17:00", "Geometry – Analytical Trigonometry", "Dr. Mona El-Sayed"),
        UpcomingSessionData(Res.string.home_day_thursday, "19:30", "Physics – Classical Mechanics & Friction", "Eng. Tarek Nour"),
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

/** Effect carrying a [StringResource] so the ViewModel stays locale-agnostic.
 *  The composable resolves it via [stringResource] before passing to the snackbar. */
sealed interface HomeTabEffect : MviEffect {
    data class ShowSnackbar(val messageRes: StringResource) : HomeTabEffect
}
