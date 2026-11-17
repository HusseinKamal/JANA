package com.hussein.jana.core.common

private var _appVersion: String = "1.0.0"

/**
 * Call once from [android.app.Application.onCreate] with [android.BuildConfig.VERSION_NAME]
 * before [initKoin] is invoked.
 *
 * ```kotlin
 * class JanaApp : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         initAppVersion(BuildConfig.VERSION_NAME)
 *         …
 *     }
 * }
 * ```
 */
fun initAppVersion(version: String) {
    _appVersion = version
}

actual val appVersion: String
    get() = _appVersion
