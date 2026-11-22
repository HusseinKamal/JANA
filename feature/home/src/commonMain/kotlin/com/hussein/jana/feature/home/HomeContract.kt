package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

enum class HomeTab { LIVE_LESSONS, MY_TEACHER_CHAT, JANA_AI, COURSES, PROFILE }

data class HomeState(
    val selectedTab: HomeTab = HomeTab.LIVE_LESSONS,
    val userName: String = "Hussein",
    val credits: Int = 12,
) : MviState

sealed interface HomeIntent : MviIntent {
    data class TabSelected(val tab: HomeTab) : HomeIntent
}

sealed interface HomeEffect : MviEffect
