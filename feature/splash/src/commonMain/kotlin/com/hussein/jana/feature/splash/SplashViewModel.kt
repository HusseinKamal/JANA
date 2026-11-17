package com.hussein.jana.feature.splash

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.datastore.TokenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenRepository: TokenRepository,
) : BaseMviViewModel<SplashState, SplashIntent, SplashEffect>(SplashState()) {

    init {
        viewModelScope.launch {
            delay(2_000)
            if (tokenRepository.isLoggedIn()) {
                sendEffect(SplashEffect.NavigateToHome)
            }
            // Guests remain on the splash screen and choose their own path.
        }
    }

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.GetStartedClicked -> viewModelScope.launch {
                sendEffect(SplashEffect.NavigateToAuth)
            }
            SplashIntent.SignInClicked -> viewModelScope.launch {
                sendEffect(SplashEffect.NavigateToSignIn)
            }
        }
    }
}
