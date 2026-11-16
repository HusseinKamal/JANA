package com.hussein.jana.feature.splash

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

data class SplashState(val isLoading: Boolean = true) : MviState

sealed interface SplashIntent : MviIntent {
    data object GetStartedClicked : SplashIntent
}

sealed interface SplashEffect : MviEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToAuth : SplashEffect
}
