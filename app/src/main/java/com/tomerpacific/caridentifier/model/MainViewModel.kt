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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _searchTermCompletionText = MutableStateFlow<CarReview?>(null)

    private val languageTranslator = LanguageTranslator()

    val searchTermCompletionText: StateFlow<CarReview?>
        get() = _searchTermCompletionText

    private val _webView = MutableStateFlow<WebView?>(null)
    val webView: StateFlow<WebView?>
        get() = _webView

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    fun triggerSnackBarEvent(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }

    init {
        _didRequestCameraPermission.value =
            _sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)
    }


    fun getCarDetails( context: Context, licensePlateNumber: String) {
        val licensePlateNumberWithoutDashes = licensePlateNumber.replace("-", "")
        viewModelScope.launch(Dispatchers.IO) {
            carDetailsRepository.getCarDetails(licensePlateNumberWithoutDashes).onSuccess {
                _carDetails.value = it
                languageTranslator.translate(concatenateCarMakeAndModel(it)).onSuccess { translatedText ->
                    searchTerm = translatedText
                    preloadWebView(context)
                }.onFailure {

                }
            }.onFailure { exception ->
                exception.localizedMessage?.let {
                    _serverError.value = when (it.contains("[")) {
                        true -> {
                            it.subSequence(
                                0,
                                it.indexOf("[")
                            ).toString()
                        }

                        false -> exception.localizedMessage
                    }
                }
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

    fun getCarReview(searchQuery: String) {

        if (_searchTermCompletionText.value != null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            carDetailsRepository.getCarReview(searchQuery)
                .onSuccess {
                _searchTermCompletionText.value = formatCarReviewResponse(it)
            }.onFailure {
                _serverError.value = it.localizedMessage
            }
        }
    }

    fun resetData() {
        _carDetails.value = null
        _serverError.value = null
        _searchTermCompletionText.value = null
        _webView.value = null
    }

    private fun preloadWebView(context: Context) {


        when (_webView.value) {
            null -> {
                viewModelScope.launch(Dispatchers.Main) {
                    _webView.value = WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.setSupportZoom(true)
                        loadUrl("https://www.youtube.com/results?search_query=$searchTerm Review")
                    }
                }
            }
        }

    }
}