package com.hussein.jana

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.hussein.jana.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "JANA",
        ) {
            App()
        }
    }
}
