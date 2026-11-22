package com.hussein.jana.core.designsystem

/**
 * Opens a URL using the platform's default handler.
 * Works for tel:, https://wa.me/, mailto:, and regular HTTPS URLs.
 */
expect fun openUrl(url: String)

/**
 * Opens the device's native calendar app.
 *  - Android  → content://com.android.calendar/time/ (native Calendar)
 *  - iOS      → calshow://  (iOS Calendar app)
 *  - Desktop  → https://calendar.google.com (web browser)
 */
fun openNativeCalendar() = openUrl(nativeCalendarUrl)
internal expect val nativeCalendarUrl: String

/**
 * Syncs a list of lessons into the device/desktop calendar.
 * [titles] and [startEpochMs] are parallel lists (same index = same lesson).
 * Returns true if at least one lesson was successfully submitted to the platform calendar.
 */
expect fun syncCalendar(titles: List<String>, startEpochMs: List<Long>): Boolean

/**
 * Previews a file at [url] with the given [mimeType] using the platform's
 * default viewer (browser, file viewer, etc.).
 */
expect fun previewFile(url: String, mimeType: String)

/** Convenience wrapper — opens a Zoom meeting URL via the platform URL handler. */
fun openZoomUrl(url: String) = openUrl(url)
