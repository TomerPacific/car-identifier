package com.tomerpacific.caridentifier.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import com.tomerpacific.caridentifier.getCarManufacturer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val DID_REQUEST_CAMERA_PERMISSION_KEY = "didRequestCameraPermission"
class MainViewModel(sharedPreferences: SharedPreferences): ViewModel() {

    private val carDetailsRepository = CarDetailsRepository()

    private val _sharedPreferences = sharedPreferences

    private val _carDetails = MutableStateFlow<CarDetails?>(null)

    val carDetails: StateFlow<CarDetails?>
        get() = _carDetails

    private val _serverError = MutableStateFlow<String?>(null)

    val serverError: StateFlow<String?>
        get() = _serverError

    private val _didRequestCameraPermission = MutableStateFlow(false)

    val didRequestCameraPermission: StateFlow<Boolean>
        get() = _didRequestCameraPermission

    private val _shouldShowRationale = MutableStateFlow(false)

    val shouldShowRationale: StateFlow<Boolean>
        get() = _shouldShowRationale

    var searchTerm: String = ""

    init {
        _didRequestCameraPermission.value =
            _sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)
    }


    fun getCarDetails(licensePlateNumber: String) {
        val licensePlateNumberWithoutDashes = licensePlateNumber.replace("-", "")
        viewModelScope.launch(Dispatchers.IO) {
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes).onSuccess { it ->
                _carDetails.value = it
                searchTerm = "${getCarManufacturer(it.manufacturerName)} ${it.commercialName.lowercase().replaceFirstChar { it.titlecase() }} ${it.trimLevel.lowercase().replaceFirstChar { it.titlecase() }} ${it.yearOfProduction} review"
            }.onFailure {
                _serverError.value = it.localizedMessage
            }
        }
    }

    fun setDidRequestCameraPermission(didRequest: Boolean) {
        if (!_didRequestCameraPermission.value) {
            _didRequestCameraPermission.value = didRequest
            _sharedPreferences.edit().putBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, didRequest).apply()
        }
    }

    fun setShouldShowRationale(shouldShow: Boolean) {
        _shouldShowRationale.value = shouldShow
    }
}