package com.example.iot_air_quality_android.data.repository

import com.example.iot_air_quality_android.data.api.RetrofitInstance
import com.example.iot_air_quality_android.data.model.response.TokenResponse

class TermsRepository {

    private val api = RetrofitInstance.api

    suspend fun fetchTerms() = api.getTerms().data?.terms ?: emptyList()

    // ✅ HD_USER 회원가입시 Role 업데이트
    suspend fun updateUserRole(): TokenResponse {
        val response = api.updateUserRole()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception("Role update failed: ${response.error}")
        }
    }
}
