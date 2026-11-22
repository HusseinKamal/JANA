package com.hussein.jana.core.designsystem

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract

actual fun openUrl(url: String) {
    _appContext?.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

internal actual val nativeCalendarUrl: String = "content://com.android.calendar/time/"

actual fun syncCalendar(titles: List<String>, startEpochMs: List<Long>): Boolean {
    val ctx = _appContext ?: return false
    if (titles.isEmpty()) return false
    var inserted = false
    titles.zip(startEpochMs).forEach { (title, startMs) ->
        try {
            val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMs)
                putExtra(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
            inserted = true
        } catch (_: Exception) { /* ignore if calendar app not available */ }
    }
    return inserted
}

actual fun previewFile(url: String, mimeType: String) {
    val ctx = _appContext ?: return
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(url), mimeType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(intent)
    } catch (_: Exception) { /* silent — no suitable viewer installed */ }
}
