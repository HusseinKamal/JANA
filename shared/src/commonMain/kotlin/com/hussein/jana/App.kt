package com.hussein.jana

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.hussein.jana.core.designsystem.AppLocaleManager
import com.hussein.jana.core.designsystem.JanaTheme
import com.hussein.jana.core.designsystem.LocalAppLanguage
import com.hussein.jana.core.designsystem.LocalAppLocale
import com.hussein.jana.core.designsystem.LocalIsDarkMode
import com.hussein.jana.core.designsystem.loadSavedDarkMode
import com.hussein.jana.core.navigation.AppNavHost
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.SplashRoute
import com.hussein.jana.core.navigation.rememberAppNavState
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    val darkModeState = remember { mutableStateOf(loadSavedDarkMode()) }

    KoinContext {
        val localeManager = koinInject<AppLocaleManager>()
        val language by localeManager.language.collectAsState()
        // Created above key(language) so the back stack, ViewModels and saved UI state
        // survive a runtime language switch.
        val navState = rememberAppNavState(SplashRoute)
        val providers = koinInject<List<FeatureEntryProvider>>()
        val layoutDir = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

        CompositionLocalProvider(
            LocalAppLanguage provides language,
            LocalAppLocale provides language.code,
            LocalIsDarkMode provides darkModeState,
            LocalLayoutDirection provides layoutDir,
        ) {
            // Compose resources 1.7.0 caches strings by Locale.current, which is not
            // observable, so the tree under the new locale has to be rebuilt.
            key(language) {
                JanaTheme(darkTheme = darkModeState.value) {
                    AppNavHost(providers = providers, navState = navState)
                }
            }
        }
    }
}
