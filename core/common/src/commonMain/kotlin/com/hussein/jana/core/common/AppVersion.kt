package com.hussein.jana.core.common

/**
 * Platform-resolved application version string.
 *
 * Sources per platform:
 *  - Android  → [BuildConfig.VERSION_NAME] (set in androidApp/build.gradle.kts defaultConfig)
 *  - iOS      → CFBundleShortVersionString from the main bundle's Info.plist
 *  - Desktop  → System property `app.version`; pass -Dapp.version=X.X.X at launch
 *
 * Always reflects the real binary version — never a stale static string.
 */
expect val appVersion: String
