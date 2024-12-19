package com.tomerpacific.caridentifier.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val carDetailsRepository = CarDetailsRepository()

    private val _carDetails = MutableStateFlow<CarDetails?>(null)

    val carDetails: StateFlow<CarDetails?>
        get() = _carDetails


    fun getCarDetails(licensePlateNumber: String) {
        val licensePlateNumberWithoutDashes = licensePlateNumber.replace("-", "")
        viewModelScope.launch(Dispatchers.IO) {
            _carDetails.value = carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes)
        }
    }
}