package com.example.iot_air_quality_android.data.api

import com.example.iot_air_quality_android.data.model.request.*
import com.example.iot_air_quality_android.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/v1/auth/login/google")
    suspend fun loginWithSocial(@Body request: LoginRequest): ResponseWrapper<TokenResponse>

    @POST("/api/v1/sensors/realtime")
    suspend fun sendSensorData(@Body request: SensorDataRequest)

    @GET("/api/v1/auth/air-quality-data/terms")
    suspend fun getTerms(): ResponseWrapper<TermsResponse>

    @PATCH("/api/v1/auth/register/air-quality-data/role")
    suspend fun updateUserRole(): ResponseWrapper<TokenResponse>

    @PATCH("/api/v1/auth/register/air-quality-data")
    suspend fun signUp(@Body request: SignUpRequest): ResponseWrapper<TokenResponse>

    @POST("/api/v1/air-quality-data/realtime")
    suspend fun sendAirQualityRealtime(@Body request: SensorDataRequest): ResponseWrapper<String?>

    @POST("/api/v1/air-quality-data/sync")
    suspend fun syncAirQualityData(@Body request: SyncRequest): ResponseWrapper<String?>

    @GET("/api/v1/air-quality-data/history")
    suspend fun getAirQualityHistory(
        @Query("projectId") projectId: Long,
        @Query("date") date: String,   // "YYYY-MM-DD"
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ResponseWrapper<HistoryListResponse>

    @GET("/api/v1/air-quality-data/projects")
    suspend fun getProjects(): ResponseWrapper<ProjectListResponse>

    @GET("/api/v1/air-quality-data/projects/participation")
    suspend fun getParticipatingProjects(): ResponseWrapper<ProjectParticipationListResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}")
    suspend fun getProjectInfo(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<ProjectInfoResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}/terms/private-policy")
    suspend fun getPrivacyPolicyTerms(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<TermsContentResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}/terms/terms-of-service")
    suspend fun getTermsOfService(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<TermsContentResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}/terms/consent-of-health-data")
    suspend fun getHealthDataConsent(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<TermsContentResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}/terms/location-data-consent")
    suspend fun getLocationDataConsent(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<TermsContentResponse>

    @GET("/api/v1/air-quality-data/projects/{projectId}/terms/air-data-consent")
    suspend fun getAirDataConsent(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<TermsContentResponse>

    @POST("/api/v1/air-quality-data/projects/{projectId}/participation")
    suspend fun participateInProject(
        @Path("projectId") projectId: Long
    ): ResponseWrapper<String?>

    @PATCH("/api/v1/auth/refresh")
    suspend fun refreshAccessToken(
        @Header("Cookie") cookie: String
    ): Response<ResponseWrapper<RefreshTokenResponse>>

    @GET("/api/auth/logout")
    suspend fun logout(): Response<Unit>
}