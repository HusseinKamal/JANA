package com.hussein.jana.core.designsystem

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "jana_prefs"
private const val KEY_DARK = "dark_mode"

private fun prefs(): SharedPreferences? =
    _appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

actual fun saveDarkMode(isDark: Boolean) {
    prefs()?.edit()?.putBoolean(KEY_DARK, isDark)?.apply()
}

actual fun loadSavedDarkMode(): Boolean =
    prefs()?.getBoolean(KEY_DARK, false) ?: false
