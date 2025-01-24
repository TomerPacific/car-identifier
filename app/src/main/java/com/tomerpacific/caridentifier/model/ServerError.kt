package com.tomerpacific.caridentifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerError(
    @SerialName("error")
    val errorMsg: String
)
