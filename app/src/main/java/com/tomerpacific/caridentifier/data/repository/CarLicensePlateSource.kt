package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.data.AppHttpClient
import com.tomerpacific.caridentifier.model.ApiResponse
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.FailureResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {

    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): ApiResponse {
        try {
            val httpResponse: HttpResponse = get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "car-license-number-fetcher.onrender.com/"
                    port = 10000
                    encodedPath = "/vehicle/${licensePlateNumber}"
                }
            }
            if (httpResponse.status.value in 200..299) {
                return httpResponse.body() as CarDetails
            }
        } catch (e: Exception) {
            print("Exception when making request ${e.message}")
        }
        return FailureResponse("Failed to get car details")
    }

    suspend fun getCarDetails(licensePlateNumber: String): ApiResponse = withContext(Dispatchers.IO) {
        client.getCarDetails(licensePlateNumber)
    }
}