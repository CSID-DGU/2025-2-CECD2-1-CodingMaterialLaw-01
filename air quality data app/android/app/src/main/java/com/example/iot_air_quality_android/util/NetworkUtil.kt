package com.example.iot_air_quality_android.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtil {

    /**
     * ✅ 현재 네트워크 연결 상태를 확인
     * @return true → Wi-Fi, 데이터, VPN 등 인터넷 사용 가능
     *         false → 연결 없음
     */
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
        } else {
            @Suppress("DEPRECATION")
            val activeNetwork = cm.activeNetworkInfo
            @Suppress("DEPRECATION")
            activeNetwork != null && activeNetwork.isConnected
        }
    }
}
