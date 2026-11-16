package com.hussein.jana.feature.home

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

data class HomeState(
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
) : MviState

sealed interface HomeIntent : MviIntent {
    data class SelectTab(val tabIndex: Int) : HomeIntent
}

sealed interface HomeEffect : MviEffect
