package com.hussein.jana.feature.auth

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.AuthRoute
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.auth.register.RegisterFlow
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
            RegisterFlow(
                onNavigateToHome    = { navigator.replaceAll(HomeRoute.Root()) },
                onNavigateToSignIn  = { navigator.replaceAll(AuthRoute.Login()) },
                onNavigateBack      = { navigator.back() },
            )
        }
        else -> null
    }
}
