package com.tomerpacific.caridentifier.model

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val DID_REQUEST_CAMERA_PERMISSION_KEY = "didRequestCameraPermission"

class PermissionViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _didRequestCameraPermission = MutableStateFlow(false)
    val didRequestCameraPermission: StateFlow<Boolean> = _didRequestCameraPermission.asStateFlow()

    private val _shouldShowRationale = MutableStateFlow(false)
    val shouldShowRationale: StateFlow<Boolean> = _shouldShowRationale.asStateFlow()

    init {
        _didRequestCameraPermission.value =
            sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)
    }

    fun setDidRequestCameraPermission(didRequest: Boolean) {
        if (_didRequestCameraPermission.value != didRequest) {
            _didRequestCameraPermission.value = didRequest
            sharedPreferences.edit { putBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, didRequest) }
        }
    }

    fun setShouldShowRationale(shouldShow: Boolean) {
        _shouldShowRationale.value = shouldShow
    }

    class Factory(
        private val sharedPreferences: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PermissionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PermissionViewModel(sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
