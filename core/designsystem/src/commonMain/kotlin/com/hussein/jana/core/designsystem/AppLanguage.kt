package com.hussein.jana.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    ARABIC("ar");

    val isRtl: Boolean get() = this == ARABIC

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

/** Current app language, provided at the app root from [AppLocaleManager]. */
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

/** Persists [language] only; switching at runtime goes through [AppLocaleManager]. */
expect fun saveLanguage(language: AppLanguage)
expect fun loadSavedLanguage(): AppLanguage
