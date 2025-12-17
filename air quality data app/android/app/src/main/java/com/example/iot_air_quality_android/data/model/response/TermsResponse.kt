package com.example.iot_air_quality_android.data.model.response

data class TermsResponse(
    val terms: List<TermItem>
)

data class TermItem(
    val type: String,
    val title: String,
    val content: String
)
