package com.example.iot_air_quality_android.ble

import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SensorDataParser {
    fun parse(data: ByteArray, latitude: Double, longitude: Double): SensorDataRequest {
        require(data.size >= 18) { "Invalid sensor data length" }

        val pm25 = (data[0].toInt() and 0xFF) * 256 + (data[1].toInt() and 0xFF)
        val pm25Level = data[2].toInt() and 0xFF

        val pm10 = (data[3].toInt() and 0xFF) * 256 + (data[4].toInt() and 0xFF)
        val pm10Level = data[5].toInt() and 0xFF

        val tempRaw = (data[6].toInt() and 0xFF) * 256 + (data[7].toInt() and 0xFF)
        val temperature = tempRaw / 10.0
        val temperatureLevel = data[8].toInt() and 0xFF

        val humidityRaw = (data[9].toInt() and 0xFF) * 256 + (data[10].toInt() and 0xFF)
        val humidity = humidityRaw / 10.0
        val humidityLevel = data[11].toInt() and 0xFF

        val co2 = (data[12].toInt() and 0xFF) * 256 + (data[13].toInt() and 0xFF)
        val co2Level = data[14].toInt() and 0xFF

        val voc = (data[15].toInt() and 0xFF) * 256 + (data[16].toInt() and 0xFF)
        val vocLevel = data[17].toInt() and 0xFF

        // 현재 시간 (서버 API 명세 맞춤 포맷)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val createdAt = LocalDateTime.now().format(formatter)

        return SensorDataRequest(
            createdAt = createdAt,
            pm25Value = pm25.toDouble(),
            pm25Level = pm25Level,
            pm10Value = pm10.toDouble(),
            pm10Level = pm10Level,
            temperature = temperature,
            temperatureLevel = temperatureLevel,
            humidity = humidity,
            humidityLevel = humidityLevel,
            co2Value = co2.toDouble(),
            co2Level = co2Level,
            vocValue = voc.toDouble(),
            vocLevel = vocLevel,
            picoDeviceLatitude = latitude,
            picoDeviceLongitude = longitude
        )
    }
}


