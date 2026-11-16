package com.hussein.jana.core.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun dispatcher(): CoroutineDispatcher = Dispatchers.Main
