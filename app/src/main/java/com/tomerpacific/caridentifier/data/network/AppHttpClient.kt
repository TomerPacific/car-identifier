package com.tomerpacific.caridentifier.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val REQUEST_TIMEOUT_DURATION = 10000L

val AppJson = Json {
    ignoreUnknownKeys = true
}

val AppHttpClient: HttpClient by lazy {
    HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_DURATION
        }
        install(ContentNegotiation) {
            json(AppJson)
        }
    }
}
