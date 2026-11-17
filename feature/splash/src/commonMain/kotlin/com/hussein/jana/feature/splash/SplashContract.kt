package com.hussein.jana.feature.splash

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

class SplashState : MviState

sealed interface SplashIntent : MviIntent {
    data object GetStartedClicked : SplashIntent
    data object SignInClicked : SplashIntent
}

sealed interface SplashEffect : MviEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToAuth : SplashEffect
    data object NavigateToSignIn : SplashEffect
}
