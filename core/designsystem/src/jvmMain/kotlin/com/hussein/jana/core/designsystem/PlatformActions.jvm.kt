package com.hussein.jana.core.designsystem

import java.awt.Desktop
import java.net.URI

actual fun openUrl(url: String) {
    try {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        }
    } catch (_: Exception) { /* silent — desktop may not support browsing */ }
}

/** Desktop opens Google Calendar in the system browser. */
internal actual val nativeCalendarUrl: String = "https://calendar.google.com"

/**
 * Syncs lessons to the desktop calendar by writing a minimal .ics file and
 * opening it with the system's default calendar application.
 */
actual fun syncCalendar(titles: List<String>, startEpochMs: List<Long>): Boolean {
    if (titles.isEmpty()) return false
    return try {
        val sb = StringBuilder()
        sb.append("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//JANA//Live Lessons//EN\r\n")
        titles.zip(startEpochMs).forEach { (title, startMs) ->
            val instant = java.time.Instant.ofEpochMilli(startMs)
            val zoned = instant.atZone(java.time.ZoneId.of("UTC"))
            val dtStart = zoned.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            )
            sb.append("BEGIN:VEVENT\r\n")
            sb.append("SUMMARY:$title\r\n")
            sb.append("DTSTART:$dtStart\r\n")
            sb.append("END:VEVENT\r\n")
        }
        sb.append("END:VCALENDAR")
        val tempFile = java.io.File.createTempFile("jana_lessons_", ".ics")
        tempFile.writeText(sb.toString())
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(tempFile)
        }
        true
    } catch (_: Exception) {
        false
    }
}

/** Preview a file by opening its URL in the system browser. */
actual fun previewFile(url: String, mimeType: String) {
    openUrl(url)
}
