package com.hussein.jana.core.common

import platform.Foundation.NSBundle

/**
 * Reads BASE_URL from Info.plist at runtime so staging/debug builds can point
 * to a different server without a code change. Falls back to the production URL
 * when the key is absent (default iOS behavior is unchanged).
 */
actual val appBaseUrl: String
    get() = NSBundle.mainBundle.infoDictionary?.get("BASE_URL") as? String
        ?: "https://api.jana.app"
