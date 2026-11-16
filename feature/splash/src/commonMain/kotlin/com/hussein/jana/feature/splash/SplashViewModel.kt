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
            autoRedirect()
        }
    }

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.GetStartedClicked -> viewModelScope.launch { redirect() }
        }
    }

    private suspend fun autoRedirect() = redirect()

    private suspend fun redirect() {
        if (tokenRepository.isLoggedIn()) {
            sendEffect(SplashEffect.NavigateToHome)
        } else {
            sendEffect(SplashEffect.NavigateToAuth)
        }
    }
}
