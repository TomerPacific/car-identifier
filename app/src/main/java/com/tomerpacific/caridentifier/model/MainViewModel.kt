package com.tomerpacific.caridentifier.model

import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.caridentifier.LanguageTranslator
import com.tomerpacific.caridentifier.concatenateCarMakeAndModel
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import com.tomerpacific.caridentifier.formatCarReviewResponse
import com.tomerpacific.caridentifier.data.network.ConnectivityObserver
import com.tomerpacific.caridentifier.data.network.NO_INTERNET_CONNECTION_ERROR
import com.tomerpacific.caridentifier.data.network.REQUEST_TIMEOUT_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DID_REQUEST_CAMERA_PERMISSION_KEY = "didRequestCameraPermission"
class MainViewModel(private val sharedPreferences: SharedPreferences,
                    private val connectivityObserver: ConnectivityObserver,
                    private val carDetailsRepository: CarDetailsRepository = CarDetailsRepository(),
                    private val languageTranslator: LanguageTranslator = LanguageTranslator()
): ViewModel() {

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

    private var searchTerm: String = ""

    private val _searchTermCompletionText = MutableStateFlow<CarReview?>(null)

    val searchTermCompletionText: StateFlow<CarReview?>
        get() = _searchTermCompletionText

    private val _webView = MutableStateFlow<WebView?>(null)
    val webView: StateFlow<WebView?>
        get() = _webView

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


    fun getCarDetails( context: Context,
                       licensePlateNumber: String? = null) {

        licensePlateNumber?.let {
            _licensePlateNumber = it
        }

        if (!connectivityObserver.isConnectedToNetwork()) {
            _serverError.value = NO_INTERNET_CONNECTION_ERROR
            return
        }

        _serverError.value = null

        val licensePlateNumberWithoutDashes = _licensePlateNumber.replace("-", "")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes).onSuccess { carDetails ->
                    _carDetails.value = carDetails
                    languageTranslator.translate(concatenateCarMakeAndModel(carDetails)).onSuccess { translatedText ->
                        searchTerm = translatedText
                        setupWebView(context)
                    }.onFailure {
                        _serverError.value = it.localizedMessage
                    }
                }.onFailure { exception ->
                    exception.localizedMessage?.let {
                        _serverError.value = when (it.contains("[")) {
                            true -> {
                                it.subSequence(
                                    0,
                                    it.indexOf("[")
                                ).toString().trim()
                            }

                            false -> exception.localizedMessage?.trim()
                        }
                    }
                }
            }
        }
    }

    fun setDidRequestCameraPermission(didRequest: Boolean) {
        if (!_didRequestCameraPermission.value) {
            _didRequestCameraPermission.value = didRequest
            sharedPreferences.edit().putBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, didRequest).apply()
        }
    }

    fun setShouldShowRationale(shouldShow: Boolean) {
        _shouldShowRationale.value = shouldShow
    }

    fun getCarReview() {

        if (!connectivityObserver.isConnectedToNetwork()) {
            _serverError.value = NO_INTERNET_CONNECTION_ERROR
            return
        }

        if (_searchTermCompletionText.value != null) {
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                carDetailsRepository.getCarReview(searchTerm)
                    .onSuccess {
                        _searchTermCompletionText.value = formatCarReviewResponse(it)
                    }.onFailure {
                        _serverError.value = it.localizedMessage
                    }
            }
        }
    }

    fun resetData() {
        _carDetails.value = null
        _serverError.value = null
        _searchTermCompletionText.value = null
        searchTerm = ""
        _webView.value = null
    }

    private fun setupWebView(context: Context) {

        if (_webView.value == null) {
            viewModelScope.launch {
                _webView.value = WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                    loadUrl("https://www.youtube.com/results?search_query=ביקורת $searchTerm")
                }
            }
        }
    }
    
    fun shouldShowRetryRequestButton(): Boolean {
        return _serverError.value == NO_INTERNET_CONNECTION_ERROR ||
                _serverError.value == REQUEST_TIMEOUT_ERROR
    }

    override fun onCleared() {
        super.onCleared()
        _webView.value?.destroy()
        connectivityObserver.unregisterNetworkCallback()
    }
}