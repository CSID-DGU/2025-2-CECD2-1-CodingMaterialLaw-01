package com.example.iot_air_quality_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_air_quality_android.data.model.response.TermItem
import com.example.iot_air_quality_android.data.repository.TermsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TermsViewModel : ViewModel() {

    private val repository = TermsRepository()

    private val _terms = MutableStateFlow<List<TermUiState>>(emptyList())
    val terms = _terms.asStateFlow()

    var userRole: String = "GUEST"

    init {
        viewModelScope.launch { loadTerms() }
    }

    private suspend fun loadTerms() {
        try {
            val list = repository.fetchTerms()
            _terms.value = list.map { TermUiState(it, false) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleTerm(type: String, checked: Boolean) {
        _terms.value = _terms.value.map {
            if (it.term.type == type) it.copy(isAgreed = checked) else it
        }
    }

    fun resetAll() {
        _terms.value = _terms.value.map { it.copy(isAgreed = false) }
    }

    val allAgreed: Boolean
        get() = _terms.value.all { it.isAgreed }

    suspend fun updateUserRole() = repository.updateUserRole()
}

data class TermUiState(
    val term: TermItem,
    val isAgreed: Boolean
)
