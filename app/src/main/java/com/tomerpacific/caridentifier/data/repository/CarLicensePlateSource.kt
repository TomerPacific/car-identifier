package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.data.network.AppHttpClient
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.ServerError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val ENDPOINT = "car-license-number-fetcher.onrender.com"
class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {

    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): Result<CarDetails> {
        val httpResponse: HttpResponse = try {
            get {
                buildUrl("/vehicle/${licensePlateNumber}")
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return when (httpResponse.status.value) {
            in 200..299 -> {
                val carDetails = httpResponse.body() as CarDetails
                return Result.success(carDetails)
            }
            else -> {
                val serverError = httpResponse.body() as ServerError
                Result.failure(Exception(serverError.errorMsg))
            }
        }
    }

    private suspend fun HttpClient.getCarReview(searchQuery: String): Result<String> {
        val httpResponse: HttpResponse = try {
            get {
                buildUrl("/review/${searchQuery}")
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return when (httpResponse.status.value) {
            in 200..299 -> {
                Result.success(httpResponse.bodyAsText())
            }

            else -> {
                val serverError = httpResponse.body() as ServerError
                Result.failure(Exception(serverError.errorMsg))
            }
        }
    }

    suspend fun getCarDetails(licensePlateNumber: String): Result<CarDetails> = withContext(Dispatchers.IO) {
        client.getCarDetails(licensePlateNumber)
    }

    suspend fun getCarReview(searchQuery: String): Result<String> = withContext(Dispatchers.IO) {
        client.getCarReview(searchQuery)
    }

    private fun buildUrl(url: String): String {
        return URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = ENDPOINT
            encodedPath = url
        }.buildString()
    }
}