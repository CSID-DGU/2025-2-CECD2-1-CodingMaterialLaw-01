package com.example.iot_air_quality_android.data.model.request

data class LoginRequest(
    val provider: String, // ex: "GOOGLE"
    val idToken: String
)