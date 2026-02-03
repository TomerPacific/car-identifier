package com.tomerpacific.caridentifier.data

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview
import com.tomerpacific.caridentifier.model.TirePressure

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val carDetails: CarDetails? = null,
    val carReview: CarReview? = null,
    val reviewUrl: String? = null,
    val tirePressure: TirePressure? = null
)
