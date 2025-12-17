package com.example.iot_air_quality_android.util


import android.content.Context
import android.content.SharedPreferences


object TokenManager {
    private const val PREF_NAME = "monorama_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"


    private lateinit var prefs: SharedPreferences


    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }


    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)


    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)


    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}