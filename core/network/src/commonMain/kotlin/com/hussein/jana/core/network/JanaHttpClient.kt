package com.hussein.jana.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient = HttpClient(provideHttpClientEngine()) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }

    install(Logging) {
        level = LogLevel.INFO
    }

    install(HttpTimeout) {
        connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT_MS
        socketTimeoutMillis = ApiConfig.SOCKET_TIMEOUT_MS
        requestTimeoutMillis = ApiConfig.REQUEST_TIMEOUT_MS
    }

    defaultRequest {
        url(ApiConfig.BASE_URL)
    }
}
