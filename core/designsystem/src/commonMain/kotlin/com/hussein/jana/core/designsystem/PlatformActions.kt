package com.hussein.jana.core.designsystem

/**
 * Opens a URL using the platform's default handler.
 * Works for tel:, https://wa.me/, mailto:, and regular HTTPS URLs.
 *
 * Platform actuals:
 *  - Android  → Intent.ACTION_VIEW via the stored Application context
 *  - iOS      → UIApplication.sharedApplication.openURL
 *  - Desktop  → java.awt.Desktop.browse
 */
expect fun openUrl(url: String)
