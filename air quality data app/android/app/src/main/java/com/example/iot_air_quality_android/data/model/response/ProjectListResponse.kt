package com.example.iot_air_quality_android.data.model.response

data class ProjectListResponse(
    val projectList: List<ProjectItemDto>
)

data class ProjectItemDto(
    val projectId: Long,
    val projectTitle: String,
    val termsOfPolicy: String,
    val privacyPolicy: String,
    val healthDataConsent: String,
    val airDataConsent: String,
    val localDataTermsOfService: String
)