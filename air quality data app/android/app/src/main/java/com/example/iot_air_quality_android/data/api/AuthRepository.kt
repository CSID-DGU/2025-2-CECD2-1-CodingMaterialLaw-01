package com.example.iot_air_quality_android.data.api

import com.example.iot_air_quality_android.data.model.request.LoginRequest
import com.example.iot_air_quality_android.data.model.response.TokenResponse

class AuthRepository {
    private val api = RetrofitInstance.api

    suspend fun loginWithGoogle(idToken: String): ResponseWrapper<TokenResponse> {
        return api.loginWithSocial(LoginRequest(provider = "GOOGLE", idToken = idToken))
    }
}