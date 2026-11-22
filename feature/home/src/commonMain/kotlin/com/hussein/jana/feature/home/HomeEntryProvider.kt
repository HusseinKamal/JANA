package com.hussein.jana.feature.home

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.hussein.jana.core.navigation.AppNavigator
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.HomeRoute
import com.hussein.jana.feature.home.tabs.live.presentation.detail.LessonDetailScreen
import org.koin.compose.koinInject

class HomeEntryProvider : FeatureEntryProvider {
    override fun entryFor(key: NavKey): NavEntry<out NavKey>? = when (key) {
        is HomeRoute.Root -> NavEntry(key) { HomeFlow() }
        is HomeRoute.LessonDetail -> NavEntry(key) {
            val navigator: AppNavigator = koinInject()
            LessonDetailScreen(
                lessonId = key.lessonId,
                onBack = { navigator.back() },
            )
        }
        else -> null
    }
}
