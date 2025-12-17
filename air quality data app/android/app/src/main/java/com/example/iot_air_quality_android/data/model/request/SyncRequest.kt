package com.example.iot_air_quality_android.data.model.request

data class SyncRequest(
    val airQualityDataList: List<SensorDataRequest>
)