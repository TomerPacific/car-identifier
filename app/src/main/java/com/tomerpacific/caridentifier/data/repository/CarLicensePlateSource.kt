package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.HEBREW_LANGUAGE_CODE
import com.tomerpacific.caridentifier.data.network.AppHttpClient
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.ServerError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val ENDPOINT = "car-license-number-fetcher.onrender.com"
private const val HTTP_STATUS_OK_LOWER_LIMIT = 200
private const val HTTP_STATUS_OK_UPPER_LIMIT = 299

class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {
    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): Result<CarDetails> {
        val httpResponse: HttpResponse =
            try {
                get {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = ENDPOINT
                        encodedPath = "/vehicle/$licensePlateNumber"
                    }
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }

        return when (httpResponse.status.value) {
            in HTTP_STATUS_OK_LOWER_LIMIT..HTTP_STATUS_OK_UPPER_LIMIT -> {
                val carDetails = httpResponse.body() as CarDetails
                Result.success(carDetails)
            }
            else -> {
                val serverError = httpResponse.body() as ServerError
                Result.failure(Exception(serverError.errorMsg))
            }
        }
    }

    private suspend fun HttpClient.getCarReview(
        searchQuery: String,
        locale: String = HEBREW_LANGUAGE_CODE,
    ): Result<String> {
        val httpResponse: HttpResponse =
            try {
                get {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = ENDPOINT
                        encodedPath = "/review/$searchQuery"
                    }
                    header("Accept-Language", locale)
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }

        return when (httpResponse.status.value) {
            in HTTP_STATUS_OK_LOWER_LIMIT..HTTP_STATUS_OK_UPPER_LIMIT -> {
                Result.success(httpResponse.bodyAsText())
            }

            else -> {
                val serverError = httpResponse.body() as ServerError
                Result.failure(Exception(serverError.errorMsg))
            }
        }
    }

    suspend fun getCarDetails(licensePlateNumber: String): Result<CarDetails> =
        withContext(Dispatchers.IO) {
            client.getCarDetails(licensePlateNumber)
        }

    suspend fun getCarReview(
        searchQuery: String,
        locale: String,
    ): Result<String> =
        withContext(Dispatchers.IO) {
            client.getCarReview(searchQuery, locale)
        }
}
