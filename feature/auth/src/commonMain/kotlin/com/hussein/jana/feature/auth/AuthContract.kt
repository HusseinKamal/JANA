package com.hussein.jana.feature.auth

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

/** Localized strings injected into [AuthViewModel] from the composition layer. */
data class AuthStrings(
    val errorMissingFields: String = "Please fill in all required fields",
    val errorMissingPlatformCode: String = "Please enter the teacher platform code",
    val googleComingSoon: String   = "Google Sign-In coming soon",
    val facebookComingSoon: String = "Facebook Sign-In coming soon",
)

data class LoginState(
    val platformCode: String       = "",
    val identifier: String        = "",
    val password: String          = "",
    val isPasswordVisible: Boolean = false,
    val rememberDevice: Boolean   = false,
    val isLoading: Boolean        = false,
    val error: String?            = null,
) : MviState

sealed interface LoginIntent : MviIntent {
    data class PlatformCodeChanged(val value: String) : LoginIntent
    data class IdentifierChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object ToggleRememberDevice : LoginIntent
    data object SignInClicked : LoginIntent
    data object GoogleSignInClicked : LoginIntent
    data object FacebookSignInClicked : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object RegisterClicked : LoginIntent
    /** Surfaced from AuthFlow to show a transient info message via the error slot. */
    data class ShowInfoMessage(val message: String) : LoginIntent
}

sealed interface LoginEffect : MviEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToRegister : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
