package com.tomerpacific.caridentifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FailureResponse(
    @SerialName("error")
    val message: String
): ApiResponse()
