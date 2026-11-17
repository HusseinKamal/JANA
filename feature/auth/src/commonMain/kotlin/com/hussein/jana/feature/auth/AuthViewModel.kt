package com.hussein.jana.feature.auth

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.datastore.TokenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    @Suppress("unused") private val tokenRepository: TokenRepository,
    private var strings: AuthStrings = AuthStrings(),
) : BaseMviViewModel<LoginState, LoginIntent, LoginEffect>(LoginState()) {

    /**
     * Called from the composition layer to supply localized strings resolved via
     * `stringResource()`. Must be invoked in a [androidx.compose.runtime.LaunchedEffect]
     * so that locale changes propagate without recreating the ViewModel.
     */
    fun updateStrings(newStrings: AuthStrings) {
        strings = newStrings
    }

    override fun onIntent(intent: LoginIntent) = when (intent) {
        is LoginIntent.IdentifierChanged    -> setState { copy(identifier = intent.value) }
        is LoginIntent.PasswordChanged      -> setState { copy(password = intent.value) }
        LoginIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
        LoginIntent.ToggleRememberDevice    -> setState { copy(rememberDevice = !rememberDevice) }
        LoginIntent.SignInClicked           -> handleSignIn()
        LoginIntent.GoogleSignInClicked     -> sendEffect(LoginEffect.ShowSnackbar(strings.googleComingSoon))
        LoginIntent.FacebookSignInClicked   -> sendEffect(LoginEffect.ShowSnackbar(strings.facebookComingSoon))
        LoginIntent.ForgotPasswordClicked   -> sendEffect(LoginEffect.NavigateToForgotPassword)
        LoginIntent.RegisterClicked         -> sendEffect(LoginEffect.NavigateToRegister)
        is LoginIntent.ShowInfoMessage      -> setState { copy(error = intent.message) }
    }

    private fun handleSignIn() {
        val state = currentState()
        if (state.identifier.isBlank() || state.password.isBlank()) {
            setState { copy(error = strings.errorMissingFields) }
            return
        }
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            delay(1_000)
            setState { copy(isLoading = false) }
            sendEffect(LoginEffect.NavigateToHome)
        }
    }
}
