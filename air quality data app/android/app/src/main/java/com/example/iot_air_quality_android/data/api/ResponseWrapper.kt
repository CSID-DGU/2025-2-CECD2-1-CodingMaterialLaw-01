package com.example.iot_air_quality_android.data.api

data class ResponseWrapper<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorInfo?
)

data class ErrorInfo(
    val message: String,
    val code: Int
)