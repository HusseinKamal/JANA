package com.hussein.jana.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.hussein.jana.feature.splash.components.SplashContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashFlow(
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit,
) {
    val viewModel: SplashViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToHome -> onNavigateToHome()
                SplashEffect.NavigateToAuth -> onNavigateToAuth()
            }
        }
    }

    SplashScreen(onGetStarted = { viewModel.onIntent(SplashIntent.GetStartedClicked) })
}

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    SplashContent(onGetStarted = onGetStarted)
}
