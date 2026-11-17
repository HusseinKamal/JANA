package com.hussein.jana.feature.auth

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.AuthRoute
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.HomeRoute
import org.koin.compose.koinInject

class AuthEntryProvider : FeatureEntryProvider {
    override fun entryFor(key: NavKey): NavEntry<out NavKey>? = when (key) {
        is AuthRoute.Login -> NavEntry(key) {
            val navigator = koinInject<AppNavigator>()
            AuthFlow(
                onNavigateToHome     = { navigator.replaceAll(HomeRoute.Root()) },
                onNavigateToRegister = { navigator.navigate(AuthRoute.Register) },
                onNavigateBack       = { navigator.back() },
            )
        }
        is AuthRoute.Register -> NavEntry(key) {
            val navigator = koinInject<AppNavigator>()
            // Registration placeholder — navigate straight to Home for now
            LaunchedEffect(Unit) { navigator.replaceAll(HomeRoute.Root()) }
        }
        else -> null
    }
}
