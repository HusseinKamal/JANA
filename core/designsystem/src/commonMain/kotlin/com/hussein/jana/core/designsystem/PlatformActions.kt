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
