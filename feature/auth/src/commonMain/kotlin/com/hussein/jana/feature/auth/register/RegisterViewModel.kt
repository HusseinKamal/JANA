package com.hussein.jana.feature.auth.register

import androidx.lifecycle.viewModelScope
import com.hussein.jana.core.common.mvi.BaseMviViewModel
import com.hussein.jana.core.datastore.TokenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(
    @Suppress("unused") private val tokenRepository: TokenRepository,
    private var strings: RegisterStrings = RegisterStrings(),
) : BaseMviViewModel<RegisterState, RegisterIntent, RegisterEffect>(RegisterState()) {

    /**
     * Called from the composition layer to supply localized error strings resolved via
     * `stringResource()`. Must be invoked in a [androidx.compose.runtime.LaunchedEffect]
     * so that locale changes propagate without recreating the ViewModel.
     */
    fun updateStrings(newStrings: RegisterStrings) {
        strings = newStrings
    }

    override fun onIntent(intent: RegisterIntent) = when (intent) {
        is RegisterIntent.FullNameChanged          -> setState { copy(fullName = intent.value, fullNameError = null) }
        is RegisterIntent.PhoneChanged             -> setState { copy(phone = intent.value, phoneError = null) }
        is RegisterIntent.PasswordChanged          -> updatePassword(intent.value)
        is RegisterIntent.ConfirmPasswordChanged   -> updateConfirmPassword(intent.value)
        RegisterIntent.TogglePasswordVisibility    -> setState { copy(isPasswordVisible = !isPasswordVisible) }
        RegisterIntent.ToggleConfirmPasswordVisibility -> setState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }
        RegisterIntent.ToggleTerms                 -> setState { copy(acceptedTerms = !acceptedTerms, termsError = null) }
        RegisterIntent.RegisterClicked             -> handleRegister()
        RegisterIntent.SignInClicked               -> sendEffect(RegisterEffect.NavigateToSignIn)
    }

    private fun updatePassword(pw: String) {
        val hasMinLength = pw.length >= 10
        val hasUpperCase = pw.any { it.isUpperCase() }
        val hasSymbol    = pw.any { !it.isLetterOrDigit() } || pw.any { it.isDigit() }

        val trueCount = listOf(hasMinLength, hasUpperCase, hasSymbol).count { it }
        val strength = when {
            pw.isEmpty()   -> PasswordStrength.NONE
            trueCount == 1 -> PasswordStrength.WEAK
            trueCount == 2 -> PasswordStrength.MEDIUM
            trueCount >= 3 -> PasswordStrength.STRONG
            else           -> PasswordStrength.NONE
        }

        setState {
            copy(
                password             = pw,
                passwordError        = null,
                passwordStrength     = strength,
                passwordHasMinLength = hasMinLength,
                passwordHasUpperCase = hasUpperCase,
                passwordHasSymbol    = hasSymbol,
            )
        }

        // Re-evaluate confirm password hint in real time
        val confirm = currentState().confirmPassword
        if (confirm.isNotEmpty()) {
            updateConfirmPassword(confirm)
        }
    }

    private fun updateConfirmPassword(value: String) {
        setState {
            copy(
                confirmPassword      = value,
                confirmPasswordError = null,
            )
        }
    }

    private fun handleRegister() {
        val s = currentState()

        val nameError = when {
            s.fullName.isBlank()         -> strings.errorNameRequired
            s.fullName.trim().length < 2 -> strings.errorNameTooShort
            else                          -> null
        }

        val phoneError = when {
            s.phone.isBlank()   -> strings.errorPhoneRequired
            s.phone.length < 7  -> strings.errorPhoneInvalid
            else                 -> null
        }

        val passwordError = when {
            s.password.length < 10  -> strings.errorPasswordTooShort
            !s.passwordHasUpperCase -> strings.errorPasswordNoUppercase
            else                     -> null
        }

        val confirmError = if (s.confirmPassword != s.password) strings.errorPasswordsMismatch else null

        val termsError = if (!s.acceptedTerms) strings.errorTermsRequired else null

        if (nameError != null || phoneError != null || passwordError != null ||
            confirmError != null || termsError != null
        ) {
            setState {
                copy(
                    fullNameError        = nameError,
                    phoneError           = phoneError,
                    passwordError        = passwordError,
                    confirmPasswordError = confirmError,
                    termsError           = termsError,
                )
            }
            return
        }

        setState { copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            delay(1_000)
            setState { copy(isLoading = false) }
            sendEffect(RegisterEffect.NavigateToHome)
        }
    }
}
