package com.hussein.jana.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    ARABIC("ar");

    val isRtl: Boolean get() = this == ARABIC

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

val LocalAppLanguage = compositionLocalOf { mutableStateOf(AppLanguage.ENGLISH) }

@Composable
fun ApplyAppLanguageLocale() {
    val language = LocalAppLanguage.current.value
    SideEffect { applyLocale(language.code) }
}

expect fun applyLocale(languageCode: String)
expect fun saveLanguage(language: AppLanguage)
expect fun saveLanguageAndRestart(language: AppLanguage)
expect fun loadSavedLanguage(): AppLanguage
