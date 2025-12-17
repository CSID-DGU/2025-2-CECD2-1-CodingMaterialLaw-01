package com.example.iot_air_quality_android.data.model.history

data class HistoryRecordUiModel(
    val id: Long,
    val measuredAtText: String,
    val pm25: MetricUi?,
    val pm10: MetricUi?,
    val temperature: MetricUi?,
    val humidity: MetricUi?,
    val co2: MetricUi?,
    val voc: MetricUi?
)

data class MetricUi(
    val title: String,
    val valueText: String,
    val status: MetricStatus
)

enum class MetricStatus { GOOD, MODERATE, BAD, VERY_BAD, UNKNOWN }