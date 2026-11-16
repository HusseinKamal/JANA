package com.hussein.jana.core.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey

interface FeatureEntryProvider {
    fun entryFor(key: NavKey): NavEntry<out NavKey>?
}
