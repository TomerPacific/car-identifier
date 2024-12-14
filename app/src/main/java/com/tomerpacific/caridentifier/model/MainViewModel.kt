package com.tomerpacific.caridentifier.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel: ViewModel() {

    private val _shouldDisplayDialogToTypeLicenseNumber = MutableStateFlow(false)
    val shouldDisplayDialogToTypeLicenseNumber: StateFlow<Boolean>
        get() = _shouldDisplayDialogToTypeLicenseNumber

    fun handleClickOnSearchOption(searchOption: LicensePlateNumberSearchOption) {
        if (searchOption == LicensePlateNumberSearchOption.TEXT) {
            _shouldDisplayDialogToTypeLicenseNumber.value = true
        }
    }

}