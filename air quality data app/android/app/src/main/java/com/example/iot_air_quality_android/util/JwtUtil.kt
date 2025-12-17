package com.example.iot_air_quality_android.util

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtUtil {
    fun parseRoleFromToken(token: String): String? {
        Log.d("AuthNavigator", "Token: $token")
        return try {
            val cleanToken = token.removePrefix("Bearer ").trim()
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            Log.d("JwtUtil", "JWT payload: $payload") // ✅ 실제 내용 찍기

            // 서버에서 "rol" 로 내려준다고 했으니까 여기로 고정
            val regex = """"rol"\s*:\s*"([A-Z_]+)"""".toRegex()
            val match = regex.find(payload)
            val role = match?.groupValues?.get(1)

            Log.d("JwtUtil", "Parsed role: $role")
            role
        } catch (e: Exception) {
            Log.e("JwtUtil", "JWT parsing failed: ${e.message}")
            null
        }
    }

    fun extractJwtExp(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payloadEncoded = parts[1]
            val payloadBytes = Base64.decode(payloadEncoded, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payloadJson = String(payloadBytes)

            val json = JSONObject(payloadJson)
            // exp가 없으면 null
            if (!json.has("exp")) return null

            json.getLong("exp")
        } catch (e: Exception) {
            null
        }
    }

    fun isJwtExpired(token: String, skewSeconds: Long = 0L): Boolean {
        val exp = extractJwtExp(token) ?: return true  // exp 못 읽으면 일단 만료로 취급
        val now = System.currentTimeMillis() / 1000L
        return now + skewSeconds >= exp
    }
}
