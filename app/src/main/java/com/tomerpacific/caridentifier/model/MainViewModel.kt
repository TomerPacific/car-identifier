package com.tomerpacific.caridentifier.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _shouldDisplayDialogToTypeLicenseNumber = MutableStateFlow(false)
    val shouldDisplayDialogToTypeLicenseNumber: StateFlow<Boolean>
        get() = _shouldDisplayDialogToTypeLicenseNumber

    private val carDetailsRepository = CarDetailsRepository()

    fun handleClickOnSearchOption(searchOption: LicensePlateNumberSearchOption) {
        if (searchOption == LicensePlateNumberSearchOption.TEXT) {
            _shouldDisplayDialogToTypeLicenseNumber.value = true
        }
    }

    fun getCarDetails(licensePlateNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val licensePlateNumberWithoutDashes = licensePlateNumber.replace("-", "")
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes)
        }
    }

    fun dismissLicensePlateInputDialog() {
        _shouldDisplayDialogToTypeLicenseNumber.value = false
    }

}