package com.example.iot_air_quality_android.data.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SensorDataRequest(
    val createdAt: String,
    val pm25Value: Double,
    val pm25Level: Int,
    val pm10Value: Double,
    val pm10Level: Int,
    val temperature: Double,
    val temperatureLevel: Int,
    val humidity: Double,
    val humidityLevel: Int,
    val co2Value: Double,
    val co2Level: Int,
    val vocValue: Double,
    val vocLevel: Int,
    val picoDeviceLatitude: Double,
    val picoDeviceLongitude: Double
) : Parcelable
