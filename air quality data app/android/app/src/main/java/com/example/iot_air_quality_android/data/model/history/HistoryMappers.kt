package com.example.iot_air_quality_android.data.model.history

import com.example.iot_air_quality_android.data.model.response.HistoryItemResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun HistoryItemResponse.toUiModel(): HistoryRecordUiModel {
    val formattedTime = try {
        LocalDateTime.parse(createdAt, serverFormatter).format(displayFormatter)
    } catch (_: Exception) {
        createdAt
    }

    return HistoryRecordUiModel(
        id = createdAt.hashCode().toLong(),
        measuredAtText = formattedTime,
        pm25 = pm25Value?.let {
            MetricUi("PM2.5", "$it μg/m³", pm25Status(it))
        },
        pm10 = pm10Value?.let {
            MetricUi("PM10", "$it μg/m³", pm10Status(it))
        },
        temperature = temperature?.let {
            MetricUi("Temperature", "$it °C", temperatureStatus(it))
        },
        humidity = humidity?.let {
            MetricUi("Humidity", "$it %", humidityStatus(it))
        },
        co2 = co2Value?.let {
            MetricUi("CO₂", "$it ppm", co2Status(it))
        },
        voc = vocValue?.let {
            MetricUi("VOC", "$it ppb", vocStatus(it))
        }
    )
}

// PM2.5 (μg/m³)
fun pm25Status(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 76 -> MetricStatus.VERY_BAD
        value >= 36 -> MetricStatus.BAD
        value >= 16 -> MetricStatus.MODERATE
        else        -> MetricStatus.GOOD
    }
}

// PM10 (μg/m³)
fun pm10Status(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 151 -> MetricStatus.VERY_BAD
        value >= 81  -> MetricStatus.BAD
        value >= 31  -> MetricStatus.MODERATE
        else         -> MetricStatus.GOOD
    }
}

// Temperature (℃) 31↑, 11~30, -9~10, -40~-10
fun temperatureStatus(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 31 -> MetricStatus.VERY_BAD
        value >= 11 -> MetricStatus.GOOD
        value >= -9 -> MetricStatus.MODERATE
        value >= -40 -> MetricStatus.BAD
        else -> MetricStatus.VERY_BAD
    }
}

// Humidity (%) 71~100, 40~70, 1~39
fun humidityStatus(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 71 -> MetricStatus.BAD
        value >= 40 -> MetricStatus.GOOD
        value >= 1  -> MetricStatus.MODERATE
        else -> MetricStatus.UNKNOWN
    }
}

// CO2 (ppm) 2500↑, 1500~2499, 1~1499
fun co2Status(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 2500 -> MetricStatus.VERY_BAD
        value >= 1500 -> MetricStatus.BAD
        value >= 1    -> MetricStatus.GOOD
        else -> MetricStatus.UNKNOWN
    }
}

// VOCs (ppb) 450↑, 250~449, 1~249
fun vocStatus(value: Double?): MetricStatus {
    if (value == null) return MetricStatus.UNKNOWN
    return when {
        value >= 450 -> MetricStatus.VERY_BAD
        value >= 250 -> MetricStatus.BAD
        value >= 1   -> MetricStatus.GOOD
        else -> MetricStatus.UNKNOWN
    }
}