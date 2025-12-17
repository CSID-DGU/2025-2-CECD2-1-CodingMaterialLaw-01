package com.example.iot_air_quality_android.data.repository

import com.example.iot_air_quality_android.data.api.ResponseWrapper
import com.example.iot_air_quality_android.data.api.RetrofitInstance
import com.example.iot_air_quality_android.data.model.response.HistoryListResponse
import com.example.iot_air_quality_android.data.model.response.ProjectParticipationListResponse

class HistoryRepository {

    private val api = RetrofitInstance.api

    private fun <T> unwrap(wrapper: ResponseWrapper<T>): T {
        if (!wrapper.success || wrapper.data == null) {
            throw IllegalStateException(wrapper.error?.message ?: "Unknown API error")
        }
        return wrapper.data
    }

    suspend fun getParticipatingProjects(): ProjectParticipationListResponse {
        val response = api.getParticipatingProjects()
        return unwrap(response)
    }

    suspend fun getAirQualityHistory(
        projectId: Long,
        date: String,
        page: Int,
        size: Int
    ): HistoryListResponse {
        val response = api.getAirQualityHistory(
            projectId = projectId,
            date = date,
            page = page,
            size = size
        )
        return unwrap(response)
    }
}