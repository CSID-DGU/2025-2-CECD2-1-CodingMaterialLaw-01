package com.example.iot_air_quality_android.data.model.response

data class HistoryListResponse(
    val historyList: List<HistoryItemResponse>,
    val hasNext: Boolean
)

data class HistoryItemResponse(
    val createdAt: String,          // "2025-11-07 07:22:58"
    val pm25Value: Double?,
    val pm10Value: Double?,
    val temperature: Double?,
    val humidity: Double?,
    val vocValue: Double?,
    val co2Value: Double?,
    val picoDeviceLatitude: Double?,
    val picoDeviceLongitude: Double?
)