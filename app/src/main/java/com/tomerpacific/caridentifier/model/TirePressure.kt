package com.tomerpacific.caridentifier.model

import kotlinx.serialization.Serializable

@Serializable
data class TirePressure(
    val source: String,
    val frontPsi: Double? = null,
    val rearPsi: Double? = null,
    val unit: String? = null,
    val note: String? = null
)
