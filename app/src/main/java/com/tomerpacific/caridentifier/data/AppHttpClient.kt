package com.tomerpacific.caridentifier.data

import io.ktor.client.HttpClient

val AppHttpClient: HttpClient by lazy {
    HttpClient()
}