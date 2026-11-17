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
