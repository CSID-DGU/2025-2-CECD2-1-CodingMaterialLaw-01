package com.example.iot_air_quality_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_air_quality_android.data.api.AuthRepository
import com.example.iot_air_quality_android.util.JwtUtil
import com.example.iot_air_quality_android.util.TokenManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    fun loginWithGoogle(idToken: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.loginWithGoogle(idToken)
                if (response.success && response.data != null) {
                    val accessToken = response.data.accessToken
                    val refreshToken = response.data.refreshToken
                    TokenManager.saveTokens(accessToken, refreshToken)
                    val role = JwtUtil.parseRoleFromToken(accessToken)
                    onResult(role)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}
