package com.hussein.jana.feature.auth.register

import com.hussein.jana.core.common.mvi.MviEffect
import com.hussein.jana.core.common.mvi.MviIntent
import com.hussein.jana.core.common.mvi.MviState

/** Localized error strings injected from the composition layer. */
data class RegisterStrings(
    val errorNameRequired: String = "",
    val errorNameTooShort: String = "",
    val errorPhoneRequired: String = "",
    val errorPhoneInvalid: String = "",
    val errorPasswordTooShort: String = "",
    val errorPasswordNoUppercase: String = "",
    val errorPasswordNoSymbol: String = "",
    val errorPasswordsMismatch: String = "",
    val errorTermsRequired: String = "",
)

enum class PasswordStrength { NONE, WEAK, MEDIUM, STRONG }

data class RegisterState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val passwordHasMinLength: Boolean = false,
    val passwordHasUpperCase: Boolean = false,
    val passwordHasSymbol: Boolean = false,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val acceptedTerms: Boolean = false,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
) : MviState

sealed interface RegisterIntent : MviIntent {
    data class FullNameChanged(val value: String) : RegisterIntent
    data class PhoneChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object ToggleConfirmPasswordVisibility : RegisterIntent
    data object ToggleTerms : RegisterIntent
    data object RegisterClicked : RegisterIntent
    data object SignInClicked : RegisterIntent
}

sealed interface RegisterEffect : MviEffect {
    data object NavigateToHome : RegisterEffect
    data object NavigateToSignIn : RegisterEffect
}
