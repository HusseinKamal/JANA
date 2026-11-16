package com.hussein.jana.core.designsystem

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

val LocalIsDarkMode = compositionLocalOf { mutableStateOf(false) }
