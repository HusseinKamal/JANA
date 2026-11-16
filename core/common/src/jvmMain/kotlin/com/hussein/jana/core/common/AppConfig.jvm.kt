package com.hussein.jana.core.common

actual val appBaseUrl: String =
    System.getProperty("BASE_URL", "http://localhost:8080")
