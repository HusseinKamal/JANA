package com.hussein.jana

import androidx.compose.ui.window.ComposeUIViewController
import com.hussein.jana.di.initKoin

fun MainViewController() = ComposeUIViewController { App() }

fun doInitKoin() {
    initKoin()
}
