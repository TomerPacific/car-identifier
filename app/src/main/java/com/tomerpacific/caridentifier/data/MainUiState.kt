package com.tomerpacific.caridentifier.data

import com.tomerpacific.caridentifier.model.CarDetails

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val carDetails: CarDetails? = null,
    val reviewUrl: String? = null
)
