package com.hussein.jana.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.hussein.jana.feature.splash.components.SplashContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashFlow(
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit,       // Get Started → registration / onboarding
    onNavigateToSignIn: () -> Unit,     // Sign In → returning-user login
) {
    val viewModel: SplashViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToHome    -> onNavigateToHome()
                SplashEffect.NavigateToAuth    -> onNavigateToAuth()
                SplashEffect.NavigateToSignIn  -> onNavigateToSignIn()
            }
        }
    }

    SplashScreen(
        onGetStarted = { viewModel.onIntent(SplashIntent.GetStartedClicked) },
        onSignIn = { viewModel.onIntent(SplashIntent.SignInClicked) },
    )
}

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
) {
    SplashContent(onGetStarted = onGetStarted, onSignIn = onSignIn)
}
