package com.hussein.jana.core.common

import platform.Foundation.NSBundle

/**
 * iOS: reads CFBundleShortVersionString from the main bundle's Info.plist.
 * Keep the Info.plist MARKETING_VERSION in sync with the Xcode project version.
 */
actual val appVersion: String
    get() = NSBundle.mainBundle
        .infoDictionary
        ?.get("CFBundleShortVersionString") as? String
        ?: "1.0.0"
