package com.hussein.jana.core.designsystem

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Suppress("DEPRECATION")
actual fun openUrl(url: String) {
    NSURL.URLWithString(url)?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}

/** iOS opens the native Calendar app via the calshow:// scheme. */
internal actual val nativeCalendarUrl: String = "calshow://"
