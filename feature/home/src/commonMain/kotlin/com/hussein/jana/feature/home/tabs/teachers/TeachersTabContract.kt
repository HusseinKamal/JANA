package com.hussein.jana.feature.home.tabs.teachers

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

// ── Badge style enum ──────────────────────────────────────────────────────────

enum class BadgeStyle { GREEN, TEAL, INDIGO, AMBER, GOLD }

// ── Data models ───────────────────────────────────────────────────────────────

data class TeacherBadgeData(
    val label: String,
    val style: BadgeStyle,
)

data class SubjectFilter(
    val key: String,
    /** Programmatic English label — UI resolves from string resources via key. */
    val label: String,
)

data class TeacherData(
    val id: String,
    val name: String,
    /** Matches a [SubjectFilter.key]. */
    val subjectKey: String,
    val subjectDetail: String,
    val rating: Float,
    val reviewCount: Int,
    val activeStudents: Int,
    val yearsExp: Int,
    val badges: List<TeacherBadgeData>,
    /** Display schedule string, e.g. "Mon • Wed • Sat (18:00 CLT)". */
    val scheduleLabel: String,
    /** Display session format string. */
    val sessionFormatLabel: String,
    val priceEgp: Int,
    /** Sub-label after price, e.g. "/ month" or "/ month + AI companion". */
    val priceSubLabel: String,
    /** Credit note below price row, e.g. "or 1 credit / session". */
    val priceNote: String,
    /** Emoji used as session type icon, e.g. "📅". */
    val sessionTypeIcon: String,
    /** Session type label, e.g. "Next Live Cohort". */
    val sessionTypeLabel: String,
)

// ── Default data ──────────────────────────────────────────────────────────────

val defaultFilters: List<SubjectFilter> = listOf(
    SubjectFilter("all", "All Subjects"),
    SubjectFilter("mathematics", "Mathematics"),
    SubjectFilter("physics", "Physics"),
    SubjectFilter("chemistry", "Chemistry"),
    SubjectFilter("biology", "Biology"),
    SubjectFilter("english", "English"),
)

val mockTeachers: List<TeacherData> = listOf(
    TeacherData(
        id = "1",
        name = "Dr. Ahmed Hassan",
        subjectKey = "mathematics",
        subjectDetail = "Algebra, Geometry, Calculus",
        rating = 4.9f,
        reviewCount = 340,
        activeStudents = 1250,
        yearsExp = 8,
        badges = listOf(
            TeacherBadgeData("99% Attendance Rate", BadgeStyle.GREEN),
            TeacherBadgeData("Avg. Response: 15 min", BadgeStyle.TEAL),
            TeacherBadgeData("Verified Lecturer", BadgeStyle.TEAL),
        ),
        scheduleLabel = "Mon • Wed • Sat (18:00 CLT)",
        sessionFormatLabel = "Group (Max 20) & 1-on-1 Slots",
        priceEgp = 650,
        priceSubLabel = "/ month",
        priceNote = "or 1 credit / session",
        sessionTypeIcon = "📅",
        sessionTypeLabel = "Next Live Cohort",
    ),
    TeacherData(
        id = "2",
        name = "Dr. Mona El-Sayed",
        subjectKey = "physics",
        subjectDetail = "Physics & Applied Mathematics",
        rating = 5.0f,
        reviewCount = 160,
        activeStudents = 820,
        yearsExp = 6,
        badges = listOf(
            TeacherBadgeData("98% Attendance", BadgeStyle.GREEN),
            TeacherBadgeData("Bilingual (AR/EN)", BadgeStyle.AMBER),
            TeacherBadgeData("AI Copilot Certified", BadgeStyle.INDIGO),
        ),
        scheduleLabel = "Tue • Thu (17:00 CLT)",
        sessionFormatLabel = "Live Whiteboard & Simulation",
        priceEgp = 700,
        priceSubLabel = "/ month + AI companion",
        priceNote = "or 1 credit / session",
        sessionTypeIcon = "📆",
        sessionTypeLabel = "Next Openings",
    ),
    TeacherData(
        id = "3",
        name = "Eng. Tarek Nour",
        subjectKey = "chemistry",
        subjectDetail = "Chemistry & Physical Science",
        rating = 4.85f,
        reviewCount = 95,
        activeStudents = 450,
        yearsExp = 5,
        badges = listOf(
            TeacherBadgeData("Small Groups (Max 8)", BadgeStyle.TEAL),
            TeacherBadgeData("Feedback in <2h", BadgeStyle.TEAL),
            TeacherBadgeData("High Retention", BadgeStyle.GREEN),
        ),
        scheduleLabel = "Starting this Sunday (19:30 CLT)",
        sessionFormatLabel = "Focus cohorts",
        priceEgp = 500,
        priceSubLabel = "/ month",
        priceNote = "or 1 credit / session",
        sessionTypeIcon = "⚡",
        sessionTypeLabel = "Live Intensive",
    ),
)

// ── Utility ───────────────────────────────────────────────────────────────────

/**
 * Formats an integer as a compact K string.
 * e.g. 1250 → "1.2k", 820 → "820"
 */
fun Int.formatK(): String =
    if (this >= 1_000) "${this / 1_000}.${(this % 1_000) / 100}k"
    else toString()

// ── MVI contract ──────────────────────────────────────────────────────────────

data class TeachersTabState(
    val searchQuery: String = "",
    val selectedFilter: String = "all",
    val filters: List<SubjectFilter> = defaultFilters,
    val allTeachers: List<TeacherData> = mockTeachers,
    val filteredTeachers: List<TeacherData> = mockTeachers,
) : MviState

sealed interface TeachersTabIntent : MviIntent {
    data class SearchQueryChanged(val query: String) : TeachersTabIntent
    data class FilterSelected(val filterKey: String) : TeachersTabIntent
    data class ProfileClicked(val teacherId: String) : TeachersTabIntent
    data class ReserveClicked(val teacherId: String) : TeachersTabIntent
    data object FilterButtonClicked : TeachersTabIntent
}

sealed interface TeachersTabEffect : MviEffect {
    data class ShowSnackbar(val message: String) : TeachersTabEffect
}
