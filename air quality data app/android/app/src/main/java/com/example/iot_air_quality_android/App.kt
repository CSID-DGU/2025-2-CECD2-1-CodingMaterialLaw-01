package com.example.iot_air_quality_android


import android.app.Application
import android.util.Log
import com.example.iot_air_quality_android.util.AuthNavigator
import com.example.iot_air_quality_android.util.NetworkLiveData
import com.example.iot_air_quality_android.util.SyncScheduler
import com.example.iot_air_quality_android.util.TokenManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        AuthNavigator.init(this)
        val networkLiveData = NetworkLiveData(this)

        networkLiveData.observeForever { isConnected ->
            Log.d("MyApplication", "Network state changed: $isConnected")
            if (isConnected == true) {
                SyncScheduler.scheduleImmediateSync(this)
            }
        }
        SyncScheduler.schedulePeriodicSync(this)
    }
}