package com.hussein.jana.core.designsystem

actual fun applyLocale(languageCode: String) {
    // iOS: RTL is handled by LocalLayoutDirection
}

actual fun loadSavedLanguage(): AppLanguage = AppLanguage.ENGLISH

actual fun saveLanguage(language: AppLanguage) {
    // iOS stub
}

actual fun saveLanguageAndRestart(language: AppLanguage) {
    // iOS stub
}
