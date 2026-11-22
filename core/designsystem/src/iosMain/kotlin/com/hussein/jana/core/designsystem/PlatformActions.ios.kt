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

/**
 * Sync lessons to calendar — opens the native iOS Calendar app via calshow://.
 * Full EventKit cinterop is not available in this setup.
 */
actual fun syncCalendar(titles: List<String>, startEpochMs: List<Long>): Boolean {
    if (titles.isEmpty()) return false
    openUrl(nativeCalendarUrl)
    return true
}

/** Preview a file by opening its URL in the platform default handler. */
actual fun previewFile(url: String, mimeType: String) {
    openUrl(url)
}
