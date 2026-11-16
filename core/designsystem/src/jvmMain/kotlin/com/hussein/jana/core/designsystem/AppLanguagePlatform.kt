package com.hussein.jana.core.designsystem

import java.util.Locale
import java.util.prefs.Preferences
import kotlin.system.exitProcess

private val prefs: Preferences = Preferences.userRoot().node("jana/language")
private const val KEY_LANGUAGE = "selected_language"

actual fun applyLocale(languageCode: String) {
    Locale.setDefault(Locale(languageCode))
}

actual fun loadSavedLanguage(): AppLanguage {
    val code = prefs.get(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
    return AppLanguage.fromCode(code)
}

actual fun saveLanguage(language: AppLanguage) {
    saveLanguageAndRestart(language)
}

actual fun saveLanguageAndRestart(language: AppLanguage) {
    prefs.put(KEY_LANGUAGE, language.code)
    prefs.flush()

    val javaBin = ProcessHandle.current().info().command().orElse(
        System.getProperty("java.home") + "/bin/java"
    )
    val cmd = ProcessHandle.current().info().arguments()
        .map { it.toList() }
        .orElse(emptyList())

    if (cmd.isNotEmpty()) {
        ProcessBuilder(listOf(javaBin) + cmd)
            .inheritIO()
            .start()
    }

    exitProcess(0)
}
