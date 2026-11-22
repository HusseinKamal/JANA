package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

enum class HomeTab { LIVE_LESSONS, MY_TEACHER_CHAT, JANA_AI, COURSES, PROFILE }

/** Decides the honorific in the title: Mr. / Mrs. UNKNOWN shows the name alone. */
enum class TeacherGender { MALE, FEMALE, UNKNOWN }

data class HomeState(
    val selectedTab: HomeTab = HomeTab.LIVE_LESSONS,
    val userName: String = "Hussein",
    val credits: Int = 12,
    /**
     * Name of the teacher whose platform the student is logged in to.
     * Shown in the Live tab title as "Jana (<teacherName>)".
     * Empty means unknown; the title then falls back to "Jana".
     * TODO: load from active teacher context (DataStore / GetActivePlatformUseCase)
     */
    val teacherName: String = "",
    /** TODO: load from active teacher context together with teacherName. */
    val teacherGender: TeacherGender = TeacherGender.UNKNOWN,
    /** Subject the teacher teaches, e.g. "Mathematics". Blank hides it in the title. */
    val teacherSubject: String = "",
) : MviState

sealed interface HomeIntent : MviIntent {
    data class TabSelected(val tab: HomeTab) : HomeIntent
}

sealed interface HomeEffect : MviEffect
