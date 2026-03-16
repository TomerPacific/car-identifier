package com.tomerpacific.caridentifier.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CAR_REVIEW_ENDPOINT = "https://www.youtube.com/results?search_query="

class CarViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val carDetailsRepository: CarDetailsRepository = CarDetailsRepository(),
    private val languageTranslator: LanguageTranslator = LanguageTranslator(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    private var searchTerm: String = ""
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()
    private var _licensePlateNumber: String = ""

    fun triggerSnackBarEvent(message: String) {
        viewModelScope.launch { _snackbarEvent.emit(message) }
    }

    fun getCarDetails(licensePlateNumber: String? = null) {
        licensePlateNumber?.let { _licensePlateNumber = it }

        if (!connectivityObserver.isConnectedToNetwork()) {
            _mainUiState.update { it.copy(isLoading = false, errorMessage = NO_INTERNET_CONNECTION_ERROR) }
            return
        }

        val licensePlateNumberWithoutDashes = _licensePlateNumber.replace("-", "")
        viewModelScope.launch(ioDispatcher) {
            _mainUiState.update { it.copy(isLoading = true, errorMessage = null) }
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes)
                .onSuccess { carDetails ->
                    val finalCarDetails =
                        if (languageTranslator.isHebrewLanguage()) {
                            val translationResult = languageTranslator.translate(
                                concatenateCarMakeAndModel(carDetails)
                            )
                            val translatedText = translationResult.getOrNull()
                            searchTerm = if (translationResult.isSuccess && !translatedText.isNullOrEmpty()) {
                                "$REVIEW_HEBREW${translatedText.first()}"
                            } else {
                                concatenateCarMakeAndModel(carDetails) + REVIEW_ENGLISH
                            }
                            carDetails
                        } else {
                            val translationResult = languageTranslator.translate(carDetails.color)
                            val translatedColor = translationResult.getOrNull()
                            val color = when {
                                translationResult.isSuccess && !translatedColor.isNullOrEmpty() -> translatedColor.first()
                                translationResult.isFailure -> FAILED_TO_TRANSLATE_MSG
                                else -> carDetails.color
                            }

                            val updatedCarDetails = carDetails.copy(
                                color = color,
                                ownership = languageTranslator.translateOwnership(carDetails.ownership),
                                fuelType = languageTranslator.translateFuelType(carDetails.fuelType)
                            )
                            searchTerm = concatenateCarMakeAndModel(updatedCarDetails) + REVIEW_ENGLISH
                            updatedCarDetails
                        }
                    withContext(mainDispatcher) {
                        _mainUiState.update {
                            it.copy(
                                isLoading = false,
                                carDetails = finalCarDetails,
                                reviewUrl = "${CAR_REVIEW_ENDPOINT}$searchTerm"
                            )
                        }
                    }
                }.onFailure { exception ->
                    val errorMessage = exception.localizedMessage?.let {
                        if (it.contains("[")) {
                            it.substring(0, it.indexOf("[")).trim()
                        } else {
                            it.trim()
                        }
                    } ?: (exception.message ?: exception.toString())

                    withContext(mainDispatcher) {
                        _mainUiState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
                    }
                }
        }
    }

    fun getCarReview() {
        if (!connectivityObserver.isConnectedToNetwork()) {
            _mainUiState.update { it.copy(isLoading = false, errorMessage = NO_INTERNET_CONNECTION_ERROR) }
            return
        }
        if (_mainUiState.value.carReview != null) return

        viewModelScope.launch(ioDispatcher) {
            _mainUiState.update { it.copy(isLoading = true) }
            carDetailsRepository.getCarReview(searchTerm, languageTranslator.currentLocale)
                .onSuccess { carReview ->
                    withContext(mainDispatcher) {
                        val formattedCarReview = formatCarReviewResponse(
                            carReview, languageTranslator)
                        _mainUiState.update {
                            it.copy(isLoading = false, carReview = formattedCarReview)
                        }
                    }
                }.onFailure { error ->
                    val errorMessage = error.localizedMessage?.let {
                        if (it.contains("[")) {
                            it.substring(0, it.indexOf("[")).trim()
                        } else {
                            it.trim()
                        }
                    } ?: (error.message ?: error.toString())
                    withContext(mainDispatcher) {
                        _mainUiState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
                    }
                }
        }
    }

    fun getTirePressure() {
        if (!connectivityObserver.isConnectedToNetwork()) {
            _mainUiState.update { it.copy(isLoading = false, errorMessage = NO_INTERNET_CONNECTION_ERROR) }
            return
        }
        if (_mainUiState.value.tirePressure != null) return

        val licensePlateNumberWithoutDashes = _licensePlateNumber.replace("-", "")
        viewModelScope.launch(ioDispatcher) {
            _mainUiState.update { it.copy(isLoading = true) }
            carDetailsRepository.getTirePressure(licensePlateNumberWithoutDashes)
                .onSuccess { tirePressure ->
                    withContext(mainDispatcher) {
                        _mainUiState.update { it.copy(isLoading = false, tirePressure = tirePressure) }
                    }
                }.onFailure { error ->
                    val errorMessage = error.localizedMessage?.let {
                        if (it.contains("[")) {
                            it.substring(0, it.indexOf("[")).trim()
                        } else {
                            it.trim()
                        }
                    } ?: (error.message ?: error.toString())
                    withContext(mainDispatcher) {
                        _mainUiState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
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
                tirePressure = null
            )
        }
    }

    fun shouldShowRetryRequestButton(): Boolean = _mainUiState.value.errorMessage in listOf(
        NO_INTERNET_CONNECTION_ERROR, REQUEST_TIMEOUT_ERROR
    )

    fun getTranslatedSectionHeader(sectionHeader: SectionHeader): String =
        languageTranslator.getSectionHeaderTitle(sectionHeader)

    override fun onCleared() {
        super.onCleared()
        connectivityObserver.unregisterNetworkCallback()
        languageTranslator.clear()
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
                val connectivityObserver = ConnectivityObserver(context.applicationContext)
                @Suppress("UNCHECKED_CAST")
                return CarViewModel(connectivityObserver) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
