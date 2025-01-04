package com.tomerpacific.caridentifier.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.data.network.NetworkError
import com.tomerpacific.caridentifier.data.network.onError
import com.tomerpacific.caridentifier.data.network.onSuccess
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(sharedPreferences: SharedPreferences): ViewModel() {

    private val carDetailsRepository = CarDetailsRepository()

    private val _sharedPreferences = sharedPreferences

    private val _carDetails = MutableStateFlow<CarDetails?>(null)

    val carDetails: StateFlow<CarDetails?>
        get() = _carDetails

    private val _networkError = MutableStateFlow<NetworkError?>(null)

    val networkError: StateFlow<NetworkError?>
        get() = _networkError

    private val _didRequestCameraPermission = MutableStateFlow(false)

    val didRequestCameraPermission: StateFlow<Boolean>
        get() = _didRequestCameraPermission

    private val _shouldShowRationale = MutableStateFlow(false)

    val shouldShowRationale: StateFlow<Boolean>
        get() = _shouldShowRationale

    init {
        _didRequestCameraPermission.value = _sharedPreferences.getBoolean("didRequestCameraPermission", false)
    }


    fun getCarDetails(licensePlateNumber: String) {
        val licensePlateNumberWithoutDashes = licensePlateNumber.replace("-", "")
        viewModelScope.launch(Dispatchers.IO) {
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes).onSuccess {
                _carDetails.value = it
            }.onError {
                _networkError.value = it
            }
        }
    }

    fun setDidRequestCameraPermission(didRequest: Boolean) {
        if (!_didRequestCameraPermission.value) {
            _didRequestCameraPermission.value = didRequest
            _sharedPreferences.edit().putBoolean("didRequestCameraPermission", didRequest).apply()
        }
    }

    fun setShouldShowRationale(shouldShow: Boolean) {
        _shouldShowRationale.value = shouldShow
    }
}