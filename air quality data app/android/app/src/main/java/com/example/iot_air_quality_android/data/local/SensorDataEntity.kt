package com.example.iot_air_quality_android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest

@Entity(tableName = "sensor_cache")
data class SensorDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
) {
    fun toRequest(): SensorDataRequest = SensorDataRequest(
        createdAt,
        pm25Value, pm25Level,
        pm10Value, pm10Level,
        temperature, temperatureLevel,
        humidity, humidityLevel,
        co2Value, co2Level,
        vocValue, vocLevel,
        picoDeviceLatitude, picoDeviceLongitude
    )
}
