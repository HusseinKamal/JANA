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
import com.hussein.jana.core.designsystem.VellumCanvas
import com.hussein.jana.feature.auth.components.AuthTopBar
import com.hussein.jana.feature.auth.components.StudentLoginContent
import com.hussein.jana.feature.auth.components.TeacherLoginContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthFlow(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    initialTab: String = "STUDENT",
) {
    val viewModel: AuthViewModel = koinViewModel()

    // Apply initialTab hint once
    LaunchedEffect(initialTab) {
        if (initialTab == "TEACHER") {
            viewModel.onIntent(LoginIntent.TabSelected(LoginTab.TEACHER))
        }
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToStudentHome -> onNavigateToHome()
                LoginEffect.NavigateToTeacherHome -> onNavigateToHome()
                LoginEffect.NavigateToRegister    -> onNavigateToRegister()
                LoginEffect.NavigateToForgotPassword -> { /* TODO: hook when ForgotPasswordRoute is wired */ }
                LoginEffect.TriggerGoogleSignIn   -> { /* Google Sign-In coming soon */ }
                LoginEffect.TriggerFacebookSignIn -> { /* Facebook Sign-In coming soon */ }
                is LoginEffect.OpenDialer   -> { /* platform intent - no-op for now */ }
                is LoginEffect.OpenWhatsApp -> { /* platform intent - no-op for now */ }
                is LoginEffect.ShowSnackbar -> { /* snackbar via scaffold - no-op for now */ }
            }
        }
    }

    AuthScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun AuthScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(VellumCanvas)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            AuthTopBar(
                isTeacherTab = state.activeTab == LoginTab.TEACHER,
                onNavigateBack = onNavigateBack,
            )
            when (state.activeTab) {
                LoginTab.STUDENT -> StudentLoginContent(
                    state = state,
                    onIntent = onIntent,
                )
                LoginTab.TEACHER -> TeacherLoginContent(
                    state = state,
                    onIntent = onIntent,
                )
            }
        }
    }
}
