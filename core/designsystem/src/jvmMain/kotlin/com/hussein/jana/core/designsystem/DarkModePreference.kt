package com.hussein.jana.core.designsystem

import java.util.prefs.Preferences

private val prefs: Preferences = Preferences.userRoot().node("jana/theme")
private const val KEY_DARK = "dark_mode"

actual fun saveDarkMode(isDark: Boolean) {
    prefs.putBoolean(KEY_DARK, isDark)
    prefs.flush()
}

actual fun loadSavedDarkMode(): Boolean = prefs.getBoolean(KEY_DARK, false)
