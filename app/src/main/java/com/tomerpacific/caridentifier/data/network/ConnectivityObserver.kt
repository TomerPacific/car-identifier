package com.tomerpacific.caridentifier.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val NO_INTERNET_CONNECTION_ERROR = "No internet connection"
const val REQUEST_TIMEOUT_ERROR = "Request timeout has expired"

class ConnectivityObserver(
    context: Context,
) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val callback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
            }

            override fun onLost(network: Network) {
                _isConnected.value = false
            }

            override fun onUnavailable() {
                _isConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val connected: Boolean = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                _isConnected.value = connected
            }
        }

    init {
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    fun isConnectedToNetwork(): Boolean {
        return _isConnected.value
    }

    fun unregisterNetworkCallback() {
        connectivityManager?.unregisterNetworkCallback(callback)
    }
}
