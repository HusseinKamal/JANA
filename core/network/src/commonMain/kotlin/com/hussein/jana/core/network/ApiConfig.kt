package com.hussein.jana.core.network

import com.hussein.jana.core.common.appBaseUrl

object ApiConfig {
    val BASE_URL get() = appBaseUrl
    const val CONNECT_TIMEOUT_MS = 15_000L
    const val SOCKET_TIMEOUT_MS = 60_000L
    const val REQUEST_TIMEOUT_MS = 60_000L
}
