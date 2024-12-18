package com.tomerpacific.caridentifier.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

val AppHttpClient: HttpClient by lazy {
    HttpClient(CIO)
}