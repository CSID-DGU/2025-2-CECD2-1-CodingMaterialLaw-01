package com.example.iot_air_quality_android.data.model.request

data class SignUpRequest(
    val name: String,
    val email: String,
    val gender: String,
    val phoneNumber: String,
    val nationalCode: String,
    val dateOfBirth: String,
    val bloodType: String,
    val height: Double,
    val weight: Double
)
