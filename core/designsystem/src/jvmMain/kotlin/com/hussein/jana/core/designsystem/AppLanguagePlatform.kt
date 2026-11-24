package com.hussein.jana.core.designsystem

import java.util.prefs.Preferences

private val prefs: Preferences = Preferences.userRoot().node("jana/language")
private const val KEY_LANGUAGE = "selected_language"

actual fun loadSavedLanguage(): AppLanguage {
    val code = prefs.get(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
    return AppLanguage.fromCode(code)
}

actual fun saveLanguage(language: AppLanguage) {
    prefs.put(KEY_LANGUAGE, language.code)
    prefs.flush()
}
