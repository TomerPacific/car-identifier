package com.tomerpacific.caridentifier.model

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.FAILED_TO_TRANSLATE_MSG
import com.tomerpacific.caridentifier.LanguageTranslator
import com.tomerpacific.caridentifier.REVIEW_ENGLISH
import com.tomerpacific.caridentifier.REVIEW_HEBREW
import com.tomerpacific.caridentifier.SectionHeader
import com.tomerpacific.caridentifier.concatenateCarMakeAndModel
import com.tomerpacific.caridentifier.data.MainUiState
import com.tomerpacific.caridentifier.data.network.ConnectivityObserver
import com.tomerpacific.caridentifier.data.network.NO_INTERNET_CONNECTION_ERROR
import com.tomerpacific.caridentifier.data.network.REQUEST_TIMEOUT_ERROR
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import com.tomerpacific.caridentifier.formatCarReviewResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DID_REQUEST_CAMERA_PERMISSION_KEY = "didRequestCameraPermission"
private const val CAR_REVIEW_ENDPOINT = "https://www.youtube.com/results?search_query="
private val TAG = MainViewModel::class.simpleName

class MainViewModel(
    private val sharedPreferences: SharedPreferences,
    private val connectivityObserver: ConnectivityObserver,
    private val carDetailsRepository: CarDetailsRepository = CarDetailsRepository(),
    private val languageTranslator: LanguageTranslator = LanguageTranslator(),
) : ViewModel() {
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    private val _didRequestCameraPermission = MutableStateFlow(false)

    val didRequestCameraPermission: StateFlow<Boolean>
        get() = _didRequestCameraPermission

    private val _shouldShowRationale = MutableStateFlow(false)

    val shouldShowRationale: StateFlow<Boolean>
        get() = _shouldShowRationale

    private var searchTerm: String = ""

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private var _licensePlateNumber: String = ""

    fun triggerSnackBarEvent(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }

    init {
        _didRequestCameraPermission.value =
            sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)
    }

    fun getCarDetails(licensePlateNumber: String? = null) {
        licensePlateNumber?.let {
            _licensePlateNumber = it
        }

        if (!connectivityObserver.isConnectedToNetwork()) {
            _mainUiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = NO_INTERNET_CONNECTION_ERROR,
                )
            }
            return
        }

        val licensePlateNumberWithoutDashes = _licensePlateNumber.replace("-", "")
        viewModelScope.launch(Dispatchers.IO) {
            _mainUiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes).onSuccess { carDetails ->

                when (languageTranslator.isHebrewLanguage()) {
                    true -> {
                        languageTranslator.translate(concatenateCarMakeAndModel(carDetails))
                            .onSuccess { translatedText ->
                                searchTerm = "$REVIEW_HEBREW${translatedText.first()}"
                            }.onFailure { error ->
                                Log.e(TAG, error.localizedMessage ?: FAILED_TO_TRANSLATE_MSG)
                                searchTerm = concatenateCarMakeAndModel(carDetails) + REVIEW_ENGLISH
                            }
                    }

                    else -> {
                        handleHebrewToEnglishTranslation(carDetails)
                    }
                }
                withContext(Dispatchers.Main) {
                    _mainUiState.update {
                        it.copy(
                            isLoading = false,
                            carDetails = carDetails,
                            reviewUrl = "${CAR_REVIEW_ENDPOINT}$searchTerm",
                        )
                    }
                }
            }.onFailure { exception ->
                exception.localizedMessage?.let {
                    val errorMessage =
                        when (it.contains("[")) {
                            true -> {
                                it.subSequence(
                                    0,
                                    it.indexOf("["),
                                ).toString().trim()
                            }

                            false -> exception.localizedMessage?.trim()
                        }

                    withContext(Dispatchers.Main) {
                        _mainUiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMessage,
                            )
                        }
                    }
                }
            }
        }
    }

    fun setDidRequestCameraPermission(didRequest: Boolean) {
        if (!_didRequestCameraPermission.value) {
            _didRequestCameraPermission.value = didRequest
            sharedPreferences.edit { putBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, didRequest) }
        }
    }

    fun setShouldShowRationale(shouldShow: Boolean) {
        _shouldShowRationale.value = shouldShow
    }

    fun getCarReview() {
        if (!connectivityObserver.isConnectedToNetwork()) {
            _mainUiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = NO_INTERNET_CONNECTION_ERROR,
                )
            }
            return
        }

        if (_mainUiState.value.carReview != null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _mainUiState.update {
                it.copy(isLoading = true)
            }
            carDetailsRepository.getCarReview(searchTerm, languageTranslator.currentLocale)
                .onSuccess { carReview ->
                    withContext(Dispatchers.Main) {
                        _mainUiState.update {
                            it.copy(
                                isLoading = false,
                                carReview = formatCarReviewResponse(carReview, languageTranslator),
                            )
                        }
                    }
                }.onFailure { error ->
                    withContext(Dispatchers.Main) {
                        _mainUiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.localizedMessage,
                            )
                        }
                    }
                }
        }
    }

    fun resetData() {
        _mainUiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                carDetails = null,
                carReview = null,
                reviewUrl = null,
            )
        }
    }

    fun shouldShowRetryRequestButton(): Boolean {
        return _mainUiState.value.errorMessage == NO_INTERNET_CONNECTION_ERROR ||
            _mainUiState.value.errorMessage == REQUEST_TIMEOUT_ERROR
    }

    fun getTranslatedSectionHeader(sectionHeader: SectionHeader): String {
        return languageTranslator.getSectionHeaderTitle(sectionHeader)
    }

    private suspend fun handleHebrewToEnglishTranslation(carDetails: CarDetails) {
        languageTranslator.translate(
            carDetails.color,
        ).onSuccess { translatedText ->
            carDetails.color = translatedText.first()
        }.onFailure {
            carDetails.color = FAILED_TO_TRANSLATE_MSG
        }

        carDetails.apply {
            ownership = languageTranslator.translateOwnership(carDetails.ownership)
            fuelType = languageTranslator.translateFuelType(carDetails.fuelType)
        }
        searchTerm = concatenateCarMakeAndModel(carDetails) + REVIEW_ENGLISH
        withContext(Dispatchers.Main) {
            _mainUiState.update {
                it.copy(
                    isLoading = false,
                    carDetails = carDetails,
                    reviewUrl = "${CAR_REVIEW_ENDPOINT}$searchTerm",
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityObserver.unregisterNetworkCallback()
        languageTranslator.clear()
    }
}
