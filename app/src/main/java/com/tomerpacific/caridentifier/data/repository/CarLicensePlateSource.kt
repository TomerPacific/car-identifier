package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.data.AppHttpClient
import com.tomerpacific.caridentifier.model.CarDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {

    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): CarDetails {
        val data = get {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.example.com"
                encodedPath = "/car-details/${licensePlateNumber}"
            }
        }
        return data.body() as CarDetails
    }

    suspend fun getCarDetails(licensePlateNumber: String): CarDetails = withContext(Dispatchers.IO) {
        client.getCarDetails(licensePlateNumber)
    }
}