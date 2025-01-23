package com.tomerpacific.caridentifier.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json

val AppHttpClient: HttpClient by lazy {
    HttpClient(OkHttp) {

        engine {
            preconfigured = okhttp3.OkHttpClient.Builder()
                .build()
        }
        install(ContentNegotiation) {
            json(
                contentType = Json,
                json = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

}