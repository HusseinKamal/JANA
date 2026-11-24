package com.hussein.jana.core.designsystem

import platform.Foundation.NSUserDefaults

private const val KEY_LANGUAGE = "jana_selected_language"

actual fun loadSavedLanguage(): AppLanguage {
    val code = NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
        ?: return AppLanguage.ENGLISH
    return AppLanguage.fromCode(code)
}

actual fun saveLanguage(language: AppLanguage) {
    NSUserDefaults.standardUserDefaults.setObject(language.code, KEY_LANGUAGE)
}
