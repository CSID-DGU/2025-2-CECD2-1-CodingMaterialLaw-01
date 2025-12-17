package com.example.iot_air_quality_android.ui.history

import com.example.iot_air_quality_android.data.model.history.HistoryRecordUiModel
import com.example.iot_air_quality_android.data.model.response.ProjectParticipationItem
import java.time.LocalDate

data class HistoryUiState(
    val projects: List<ProjectParticipationItem> = emptyList(),
    val selectedProjectId: Long? = null,
    val selectedDate: LocalDate? = null,
    val records: List<HistoryRecordUiModel> = emptyList(),
    val page: Int = 0,
    val hasNext: Boolean = true,
    val isLoading: Boolean = false
)