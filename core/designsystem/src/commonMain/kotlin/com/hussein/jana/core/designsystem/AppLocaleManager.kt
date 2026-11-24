package com.hussein.jana.core.designsystem

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for the app language. The app root collects [language] and
 * re-provides the locale; features call [setLanguage] to switch at runtime.
 *
 * The language is persisted in synchronous platform prefs so the root knows the layout
 * direction before the first frame.
 */
class AppLocaleManager(initial: AppLanguage = loadSavedLanguage()) {

    private val _language = MutableStateFlow(initial)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        if (language == _language.value) return
        saveLanguage(language)
        _language.value = language
    }
}
