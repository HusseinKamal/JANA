package com.hussein.jana.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hussein.jana.core.designsystem.Res
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.core.designsystem.auth_error_missing_fields
import com.hussein.jana.core.designsystem.auth_facebook_coming_soon
import com.hussein.jana.core.designsystem.auth_google_coming_soon
import com.hussein.jana.feature.auth.components.StudentLoginContent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful entry point for the student login screen.
 * Collects one-shot [LoginEffect]s and delegates navigation to callbacks.
 * No top bar — the screen is fully edge-to-edge with status bar insets
 * handled inside [StudentLoginContent].
 */
@Composable
fun AuthFlow(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,   // kept for navigation compatibility
) {
    val viewModel: AuthViewModel = koinViewModel()

    val errorMissingFields = stringResource(Res.string.auth_error_missing_fields)
    val googleComingSoon   = stringResource(Res.string.auth_google_coming_soon)
    val facebookComingSoon = stringResource(Res.string.auth_facebook_coming_soon)
    LaunchedEffect(errorMissingFields, googleComingSoon, facebookComingSoon) {
        viewModel.updateStrings(
            AuthStrings(
                errorMissingFields = errorMissingFields,
                googleComingSoon   = googleComingSoon,
                facebookComingSoon = facebookComingSoon,
            ),
        )
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome           -> onNavigateToHome()
                LoginEffect.NavigateToRegister       -> onNavigateToRegister()
                LoginEffect.NavigateToForgotPassword -> { /* TODO: wire ForgotPasswordRoute */ }
                is LoginEffect.ShowSnackbar          ->
                    viewModel.onIntent(LoginIntent.ShowInfoMessage(effect.message))
            }
        }
    }

    AuthScreen(state = state, onIntent = viewModel::onIntent)
}

/** Stateless student login screen. No top bar — fully edge-to-edge. */
@Composable
fun AuthScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(VellumCanvas)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            StudentLoginContent(state = state, onIntent = onIntent)
        }
    }
}
