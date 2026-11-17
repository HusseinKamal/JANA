package com.hussein.jana.feature.auth

import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.datastore.TokenRepository
import androidx.lifecycle.viewModelScope
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

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.TabSelected ->
                setState { copy(activeTab = intent.tab, error = null) }
            is LoginIntent.IdentifierChanged ->
                setState { copy(identifier = intent.value) }
            is LoginIntent.PasswordChanged ->
                setState { copy(password = intent.value) }
            LoginIntent.TogglePasswordVisibility ->
                setState { copy(isPasswordVisible = !isPasswordVisible) }
            LoginIntent.ToggleRememberDevice ->
                setState { copy(rememberDevice = !rememberDevice) }
            LoginIntent.ToggleKeepSession ->
                setState { copy(keepSession = !keepSession) }
            LoginIntent.SignInClicked ->
                handleSignIn()
            LoginIntent.GoogleSignInClicked ->
                sendEffect(LoginEffect.TriggerGoogleSignIn)
            LoginIntent.FacebookSignInClicked ->
                sendEffect(LoginEffect.TriggerFacebookSignIn)
            LoginIntent.ForgotPasswordClicked ->
                sendEffect(LoginEffect.NavigateToForgotPassword)
            LoginIntent.RegisterClicked ->
                sendEffect(LoginEffect.NavigateToRegister)
            LoginIntent.InstitutionalSSOClicked ->
                sendEffect(LoginEffect.ShowSnackbar(strings.errorSsoComingSoon))
            LoginIntent.CallAdmissionsClicked ->
                sendEffect(LoginEffect.OpenDialer("+20237498000"))
            LoginIntent.WhatsAppFacultyClicked ->
                sendEffect(LoginEffect.OpenWhatsApp("+20237498000"))
        }
    }

    private fun handleSignIn() {
        val state = currentState()
        if (state.identifier.isBlank() || state.password.isBlank()) {
            setState { copy(error = strings.errorMissingFields) }
            return
        }
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            delay(1000)
            setState { copy(isLoading = false) }
            if (currentState().activeTab == LoginTab.STUDENT) {
                sendEffect(LoginEffect.NavigateToStudentHome)
            } else {
                sendEffect(LoginEffect.NavigateToTeacherHome)
            }
        }
    }
}
