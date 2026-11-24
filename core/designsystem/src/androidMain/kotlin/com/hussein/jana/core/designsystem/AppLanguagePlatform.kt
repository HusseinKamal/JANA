package com.hussein.jana.core.designsystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

private const val PREFS_NAME = "jana_prefs"
private const val KEY_LANGUAGE = "selected_language"

internal var _appContext: Context? = null

fun initAppContext(context: Context) {
    _appContext = context.applicationContext
}

private fun prefs(): SharedPreferences? =
    _appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

actual fun saveLanguage(language: AppLanguage) {
    prefs()?.edit()?.putString(KEY_LANGUAGE, language.code)?.apply()
}

actual fun loadSavedLanguage(): AppLanguage {
    val saved = prefs()?.getString(KEY_LANGUAGE, null)
    if (saved != null) return AppLanguage.fromCode(saved)
    val delegateLocale = AppCompatDelegate.getApplicationLocales()
    if (!delegateLocale.isEmpty) {
        val tag = delegateLocale[0]?.toLanguageTag() ?: return AppLanguage.ENGLISH
        return AppLanguage.fromCode(tag.substringBefore("-"))
    }
    return AppLanguage.ENGLISH
}
