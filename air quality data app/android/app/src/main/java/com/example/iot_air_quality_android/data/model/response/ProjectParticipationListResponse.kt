package com.example.iot_air_quality_android.data.model.response

data class ProjectParticipationListResponse(
    val projects: List<ProjectParticipationItem>
)

data class ProjectParticipationItem(
    val projectId: Long,
    val title: String
)