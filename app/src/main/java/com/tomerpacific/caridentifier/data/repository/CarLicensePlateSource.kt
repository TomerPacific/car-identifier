package com.tomerpacific.caridentifier.data.repository

import com.tomerpacific.caridentifier.data.network.AppHttpClient
import com.tomerpacific.caridentifier.data.network.NetworkError
import com.tomerpacific.caridentifier.data.network.Result
import com.tomerpacific.caridentifier.model.CarDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException

class CarLicensePlateSource(private val client: HttpClient = AppHttpClient) {

    private suspend fun HttpClient.getCarDetails(licensePlateNumber: String): Result<CarDetails, NetworkError> {
        val httpResponse: HttpResponse = try {
            get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "car-license-number-fetcher.onrender.com"
                    encodedPath = "/vehicle/${licensePlateNumber}"
                }
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when (httpResponse.status.value) {
            in 200..299 -> {
                val carDetails = httpResponse.body() as CarDetails
                Result.Success(carDetails)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            404 -> Result.Error(NetworkError.NOT_FOUND)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            409 -> Result.Error(NetworkError.CONFLICT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..513 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getCarDetails(licensePlateNumber: String): Result<CarDetails, NetworkError> = withContext(Dispatchers.IO) {
        client.getCarDetails(licensePlateNumber)
    }
}