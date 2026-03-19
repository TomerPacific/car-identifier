package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.BuildConfig
import com.tomerpacific.caridentifier.HEBREW_LANGUAGE_CODE
import com.tomerpacific.caridentifier.data.network.AppHttpClient
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.ServerError
import com.tomerpacific.caridentifier.model.TirePressure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val HTTP_STATUS_OK_LOWER_LIMIT = 200
private const val HTTP_STATUS_OK_UPPER_LIMIT = 299

class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {
    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): Result<CarDetails> {
        return try {
            val httpResponse: HttpResponse = get("${BuildConfig.BASE_URL}/vehicle/$licensePlateNumber")

            when (httpResponse.status.value) {
                in HTTP_STATUS_OK_LOWER_LIMIT..HTTP_STATUS_OK_UPPER_LIMIT -> {
                    val carDetails = httpResponse.body<CarDetails>()
                    Result.success(carDetails)
                }
                else -> {
                    Result.failure(Exception(httpResponse.getErrorMessage()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.failure(e)
        }
    }

    private suspend fun HttpClient.getTirePressure(licensePlateNumber: String): Result<TirePressure> {
        return try {
            val httpResponse: HttpResponse = get("${BuildConfig.BASE_URL}/tire-pressure/$licensePlateNumber")

            when (httpResponse.status.value) {
                in HTTP_STATUS_OK_LOWER_LIMIT..HTTP_STATUS_OK_UPPER_LIMIT -> {
                    val tirePressure = httpResponse.body<TirePressure>()
                    Result.success(tirePressure)
                }
                else -> {
                    Result.failure(Exception(httpResponse.getErrorMessage()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.failure(e)
        }
    }


    private suspend fun HttpClient.getCarReview(
        searchQuery: String,
        locale: String = HEBREW_LANGUAGE_CODE,
    ): Result<String> {
        return try {
            val httpResponse: HttpResponse = get("${BuildConfig.BASE_URL}/review/$searchQuery") {
                header("Accept-Language", locale)
            }

            when (httpResponse.status.value) {
                in HTTP_STATUS_OK_LOWER_LIMIT..HTTP_STATUS_OK_UPPER_LIMIT -> {
                    Result.success(httpResponse.bodyAsText())
                }

                else -> {
                    Result.failure(Exception(httpResponse.getErrorMessage()))
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.failure(e)
        }
    }

    private suspend fun HttpResponse.getErrorMessage(): String {
        return try {
            this.body<ServerError>().errorMsg
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            this.bodyAsText()
        }
    }

    suspend fun getCarDetails(licensePlateNumber: String): Result<CarDetails> =
        withContext(Dispatchers.IO) {
            client.getCarDetails(licensePlateNumber)
        }

    suspend fun getTirePressure(licensePlateNumber: String): Result<TirePressure> =
        withContext(Dispatchers.IO) {
            client.getTirePressure(licensePlateNumber)
        }

    suspend fun getCarReview(
        searchQuery: String,
        locale: String,
    ): Result<String> =
        withContext(Dispatchers.IO) {
            client.getCarReview(searchQuery, locale)
        }
}
