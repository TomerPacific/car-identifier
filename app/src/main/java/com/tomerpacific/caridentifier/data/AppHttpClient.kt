package com.tomerpacific.caridentifier.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val AppHttpClient: HttpClient by lazy {
    HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }
}