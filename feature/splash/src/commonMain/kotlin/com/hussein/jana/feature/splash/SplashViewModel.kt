package com.hussein.jana.feature.splash

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.datastore.TokenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SplashViewModel(
    private val tokenRepository: TokenRepository,
) : BaseMviViewModel<SplashState, SplashIntent, SplashEffect>(SplashState()) {

    /**
     * Guards against double-navigation: once any navigation effect is sent
     * (auto-redirect OR a user tap), all subsequent attempts are ignored.
     * This prevents a race where a logged-in user taps "Get Started" just
     * before the 2 s timer fires, which would otherwise emit two effects.
     *
     * Access is safe without synchronisation because [viewModelScope] always
     * runs on the Main dispatcher (single-threaded for UI work).
     */
    private var navigated = false

    init {
        viewModelScope.launch {
            delay(2_000.milliseconds)
            if (tokenRepository.isLoggedIn()) {
                navigateOnce(SplashEffect.NavigateToHome)
            }
            // Guests remain on the splash screen and choose their own path.
        }
    }

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.GetStartedClicked -> navigateOnce(SplashEffect.NavigateToAuth)
            SplashIntent.SignInClicked     -> navigateOnce(SplashEffect.NavigateToSignIn)
        }
    }

    private fun navigateOnce(effect: SplashEffect) {
        if (navigated) return
        navigated = true
        sendEffect(effect)
    }
}
