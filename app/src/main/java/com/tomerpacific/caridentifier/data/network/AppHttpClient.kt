package com.tomerpacific.caridentifier.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import okhttp3.CertificatePinner

val AppHttpClient: HttpClient by lazy {
    HttpClient(OkHttp) {
        engine {
            preconfigured = okhttp3.OkHttpClient.Builder()
                .certificatePinner(
                CertificatePinner.Builder()
                    .add("car-license-number-fetcher.onrender.com",
                        "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c=")
                    .build())
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