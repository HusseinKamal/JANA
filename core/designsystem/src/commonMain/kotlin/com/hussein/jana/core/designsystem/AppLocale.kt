package com.hussein.jana.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Overrides the locale Compose resources read, per platform.
 *
 * Provide it at the app root together with `key(languageCode)` so the tree is rebuilt and
 * `stringResource` picks up the new locale (resources 1.7.0 caches by `Locale.current`,
 * which is not observable). Pattern from
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html
 *
 * Passing `null` restores the system locale.
 */
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}
