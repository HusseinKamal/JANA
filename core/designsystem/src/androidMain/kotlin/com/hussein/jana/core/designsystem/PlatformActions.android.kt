package com.hussein.jana.core.designsystem

import android.content.Intent
import android.net.Uri

actual fun openUrl(url: String) {
    _appContext?.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

internal actual val nativeCalendarUrl: String = "content://com.android.calendar/time/"
