package com.example.iot_air_quality_android.data.repository

import com.example.iot_air_quality_android.data.api.RetrofitInstance
import com.example.iot_air_quality_android.data.model.request.SignUpRequest
import com.example.iot_air_quality_android.data.model.response.TokenResponse
import com.example.iot_air_quality_android.data.api.ResponseWrapper

class SignUpRepository {
    suspend fun signUp(request: SignUpRequest): ResponseWrapper<TokenResponse> {
        return RetrofitInstance.api.signUp(request)
    }
}
