// ui/history/HistoryViewModel.kt
package com.example.iot_air_quality_android.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot_air_quality_android.data.model.history.toUiModel
import com.example.iot_air_quality_android.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryViewModel(
    private val repository: HistoryRepository = HistoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadProjects()
    }

    private fun update(block: (HistoryUiState) -> HistoryUiState) {
        _uiState.value = block(_uiState.value)
    }

    // 참여중 프로젝트 목록
    private fun loadProjects() {
        viewModelScope.launch {
            try {
                val data = repository.getParticipatingProjects()
                update { it.copy(projects = data.projects) }
            } catch (_: Exception) {
                // 필요하면 에러 상태 추가
            }
        }
    }

    fun selectProject(projectId: Long?) {
        update {
            it.copy(
                selectedProjectId = projectId,
                page = 0,
                hasNext = true,
                records = emptyList()
            )
        }
        reloadIfReady()
    }

    fun selectDate(date: LocalDate?) {
        update {
            it.copy(
                selectedDate = date,
                page = 0,
                hasNext = true,
                records = emptyList()
            )
        }
        reloadIfReady()
    }

    private fun reloadIfReady() {
        val state = _uiState.value
        if (state.selectedProjectId != null && state.selectedDate != null) {
            loadHistory(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.isLoading && state.hasNext) {
            loadHistory(page = state.page + 1, append = true)
        }
    }

    private fun loadHistory(page: Int, append: Boolean) {
        val state = _uiState.value
        val projectId = state.selectedProjectId ?: return
        val date = state.selectedDate ?: return

        update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val result = repository.getAirQualityHistory(
                    projectId = projectId,
                    date = date.toString(),  // "YYYY-MM-DD"
                    page = page,
                    size = 50
                )

                val newRecords = result.historyList.map { it.toUiModel() }

                update {
                    it.copy(
                        records = if (append) it.records + newRecords else newRecords,
                        page = page,
                        hasNext = result.hasNext,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                update { it.copy(isLoading = false) }
            }
        }
    }
}
