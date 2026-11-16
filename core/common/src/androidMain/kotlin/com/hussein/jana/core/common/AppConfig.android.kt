package com.hussein.jana.core.common

private var _url: String? = null

/**
 * Call this from Application.onCreate() before initKoin():
 *   initAppBaseUrl(BuildConfig.BASE_URL)
 */
fun initAppBaseUrl(url: String) {
    _url = url
}

actual val appBaseUrl: String
    get() = _url ?: "http://localhost:8080"
