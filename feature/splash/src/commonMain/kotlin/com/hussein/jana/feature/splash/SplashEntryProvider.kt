package com.hussein.jana.feature.splash

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.AuthRoute
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.core.navigation.SplashRoute
import org.koin.compose.koinInject

class SplashEntryProvider : FeatureEntryProvider {
    override fun entryFor(key: NavKey): NavEntry<out NavKey>? = when (key) {
        is SplashRoute -> NavEntry(key) {
            val navigator = koinInject<AppNavigator>()
            SplashFlow(
                onNavigateToHome = { navigator.replaceAll(HomeRoute.Root()) },
                onNavigateToAuth = { navigator.replaceAll(AuthRoute.Login) },
            )
        }
        else -> null
    }
}
