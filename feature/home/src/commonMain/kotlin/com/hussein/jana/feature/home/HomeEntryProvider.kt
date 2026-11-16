package com.hussein.jana.feature.home

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.hussein.jana.core.navigation.FeatureEntryProvider
import com.hussein.jana.core.navigation.HomeRoute

class HomeEntryProvider : FeatureEntryProvider {
    override fun entryFor(key: NavKey): NavEntry<out NavKey>? = when (key) {
        is HomeRoute.Root -> NavEntry(key) { HomeFlow() }
        else -> null
    }
}
