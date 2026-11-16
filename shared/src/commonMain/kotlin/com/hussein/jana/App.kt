package com.hussein.jana

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.hussein.jana.core.designsystem.ApplyAppLanguageLocale
import com.hussein.jana.core.designsystem.JanaTheme
import com.hussein.jana.core.designsystem.LocalAppLanguage
import com.hussein.jana.core.designsystem.LocalIsDarkMode
import com.hussein.jana.core.designsystem.loadSavedDarkMode
import com.hussein.jana.core.designsystem.loadSavedLanguage
import com.hussein.jana.core.navigation.AppNavHost
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.SplashRoute
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    val darkModeState = remember { mutableStateOf(loadSavedDarkMode()) }
    val languageState = remember { mutableStateOf(loadSavedLanguage()) }

    KoinContext {
        val layoutDir = if (languageState.value.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        CompositionLocalProvider(
            LocalAppLanguage provides languageState,
            LocalIsDarkMode provides darkModeState,
            LocalLayoutDirection provides layoutDir,
        ) {
            ApplyAppLanguageLocale()
            JanaTheme(darkTheme = darkModeState.value) {
                val providers = koinInject<List<FeatureEntryProvider>>()
                AppNavHost(providers = providers, startDestination = SplashRoute)
            }
        }
    }
}
