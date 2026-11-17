package com.hussein.jana.feature.auth

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

enum class LoginTab { STUDENT, TEACHER }

/** Localized strings injected into [AuthViewModel] from the composition layer. */
data class AuthStrings(
    val errorMissingFields: String = "Please fill in all required fields",
    val errorSsoComingSoon: String = "SSO coming soon",
)

data class LoginState(
    val activeTab: LoginTab = LoginTab.STUDENT,
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberDevice: Boolean = false,
    val keepSession: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
) : MviState

sealed interface LoginIntent : MviIntent {
    data class TabSelected(val tab: LoginTab) : LoginIntent
    data class IdentifierChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object ToggleRememberDevice : LoginIntent
    data object ToggleKeepSession : LoginIntent
    data object SignInClicked : LoginIntent
    data object GoogleSignInClicked : LoginIntent
    data object FacebookSignInClicked : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object RegisterClicked : LoginIntent
    data object InstitutionalSSOClicked : LoginIntent
    data object CallAdmissionsClicked : LoginIntent
    data object WhatsAppFacultyClicked : LoginIntent
}

sealed interface LoginEffect : MviEffect {
    data object NavigateToStudentHome : LoginEffect
    data object NavigateToTeacherHome : LoginEffect
    data object NavigateToRegister : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data object TriggerGoogleSignIn : LoginEffect
    data object TriggerFacebookSignIn : LoginEffect
    data class OpenDialer(val number: String) : LoginEffect
    data class OpenWhatsApp(val number: String) : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
