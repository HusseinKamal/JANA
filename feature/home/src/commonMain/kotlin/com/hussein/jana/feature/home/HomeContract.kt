package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

enum class HomeTab { HOME, TEACHERS, JANA_AI, SCHEDULE, PROGRESS }

data class HomeState(
    val selectedTab: HomeTab = HomeTab.HOME,
    val userName: String = "Hussein",
    val credits: Int = 12,
) : MviState

sealed interface HomeIntent : MviIntent {
    data class TabSelected(val tab: HomeTab) : HomeIntent
}

sealed interface HomeEffect : MviEffect
