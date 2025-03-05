package com.tomerpacific.caridentifier.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.getSystemService

const val NO_INTERNET_CONNECTION_ERROR = "No internet connection"
class ConnectivityObserver(
    context: Context
) {



    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    private val isConnected : MutableState<Boolean> = mutableStateOf(false)

    private val callback = object: ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            isConnected.value = true
        }

        override fun onLost(network: Network) {
            isConnected.value = false
        }

        override fun onUnavailable() {
            isConnected.value = false
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val connected: Boolean = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            isConnected.value = connected
        }
    }

    init {
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    fun isConnectedToNetwork(): Boolean {
        return isConnected.value
    }

    fun unregisterNetworkCallback() {
        connectivityManager?.unregisterNetworkCallback(callback)
    }
}