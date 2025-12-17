package com.example.iot_air_quality_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_air_quality_android.data.model.request.SignUpRequest
import com.example.iot_air_quality_android.data.model.response.TokenResponse
import com.example.iot_air_quality_android.data.repository.SignUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: SignUpRepository = SignUpRepository()
) : ViewModel() {

    private val _signUpState = MutableStateFlow<ResultState>(ResultState.Idle)
    val signUpState: StateFlow<ResultState> = _signUpState

    fun signUp(request: SignUpRequest) {
        viewModelScope.launch {
            try {
                _signUpState.value = ResultState.Loading
                val response = repository.signUp(request)

                if (response.success) {
                    _signUpState.value = ResultState.Success(response.data)
                } else {
                    val errorInfo = response.error
                    val message = errorInfo?.message ?: "Unknown server error"
                    _signUpState.value = ResultState.Error(message)
                }
            } catch (e: Exception) {
                _signUpState.value = ResultState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    sealed class ResultState {
        object Idle : ResultState()
        object Loading : ResultState()
        data class Success(val data: TokenResponse?) : ResultState()
        data class Error(val message: String) : ResultState()
    }
}
