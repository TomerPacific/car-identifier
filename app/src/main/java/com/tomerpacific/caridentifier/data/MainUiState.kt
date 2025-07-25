package com.tomerpacific.caridentifier.data

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val carDetails: CarDetails? = null,
    val carReview: CarReview? = null,
    val reviewUrl: String? = null,
)
