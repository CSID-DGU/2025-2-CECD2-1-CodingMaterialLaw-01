package com.example.iot_air_quality_android.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

/**
 * 실시간 네트워크 상태 감시 LiveData
 */
class NetworkLiveData(private val context: Context) : LiveData<Boolean>() {
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, callback)

        // ✅ context를 직접 참조
        postValue(NetworkUtil.isConnected(context))
    }

    override fun onInactive() {
        super.onInactive()
        try {
            cm.unregisterNetworkCallback(callback)
        } catch (_: Exception) {}
    }
}
