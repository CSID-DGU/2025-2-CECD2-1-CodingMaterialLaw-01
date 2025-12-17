package com.example.iot_air_quality_android.data.api

import android.util.Log
import com.example.iot_air_quality_android.util.AuthNavigator
import com.example.iot_air_quality_android.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection

class AuthInterceptor : Interceptor {

    @Volatile
    private var isRefreshing: Boolean = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val accessToken = TokenManager.getAccessToken()

        // 1) Authorization 헤더 자동 추가 (이미 있으면 건드리지 않음)
        if (!accessToken.isNullOrEmpty() && originalRequest.header("Authorization") == null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val request = requestBuilder.build()
        var response = chain.proceed(request)

        // 2) 401 아니거나, 이미 재시도한 요청이면 그대로 반환
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED ||
            originalRequest.header("X-Reauth-Attempt") != null
        ) {
            return response
        }

        // 여기부터는 401 + 아직 재시도 안 한 요청
        response.close()

        synchronized(this) {
            // 3) 현재 내가 refresh 담당
            if (!isRefreshing) {
                isRefreshing = true
                Log.w("AuthInterceptor", "401 detected → try refresh token")

                val newAccessToken = refreshAccessTokenBlocking()

                isRefreshing = false

                return if (!newAccessToken.isNullOrEmpty()) {
                    Log.d("AuthInterceptor", "Refresh success → retry original request")

                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .header("X-Reauth-Attempt", "true")
                        .build()

                    chain.proceed(newRequest)
                } else {
                    Log.e("AuthInterceptor", "Refresh failed → force logout")

                    TokenManager.clearTokens()
                    AuthNavigator.forceLogoutToLogin()

                    // 실패 응답 그대로 돌려주기
                    response.newBuilder()
                        .code(HttpURLConnection.HTTP_UNAUTHORIZED)
                        .build()
                }
            } else {
                // 4) 다른 쓰레드가 이미 refresh 중 → 나는 최신 토큰으로 재시도만
                Log.d("AuthInterceptor", "Already refreshing → wait & retry with latest token")

                val latestToken = TokenManager.getAccessToken()

                return if (!latestToken.isNullOrEmpty()) {
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $latestToken")
                        .header("X-Reauth-Attempt", "true")
                        .build()

                    chain.proceed(newRequest)
                } else {
                    // 여전히 토큰 없으면 그냥 로그아웃 처리
                    TokenManager.clearTokens()
                    AuthNavigator.forceLogoutToLogin()

                    response.newBuilder()
                        .code(HttpURLConnection.HTTP_UNAUTHORIZED)
                        .build()
                }
            }
        }
    }

    /**
     * 🔁 refresh API 동기 호출 (인터셉터 없는 Retrofit 사용)
     */
    private fun refreshAccessTokenBlocking(): String? {
        val currentRefreshToken = TokenManager.getRefreshToken()

        if (currentRefreshToken.isNullOrEmpty()) {
            Log.e("AuthInterceptor", "No refreshToken found → cannot refresh")
            return null
        }

        return try {
            val retrofitResponse = runBlocking {
                RetrofitRefreshInstance.api.refreshAccessToken(
                    cookie = "refreshToken=$currentRefreshToken"
                )
            }

            if (!retrofitResponse.isSuccessful) {
                Log.e("AuthInterceptor", "refresh failed: HTTP ${retrofitResponse.code()}")
                return null
            }

            val body = retrofitResponse.body()
            if (body?.success != true || body.data == null) {
                Log.e(
                    "AuthInterceptor",
                    "refresh failed: success=${body?.success}, error=${body?.error}"
                )
                return null
            }

            val newAccessToken = body.data.accessToken
            if (newAccessToken.isNullOrEmpty()) {
                Log.e("AuthInterceptor", "refresh failed: data.accessToken is null")
                return null
            }

            // 🔍 Set-Cookie 에서 새 refreshToken 있으면 파싱
            val setCookieHeaders = retrofitResponse.headers().values("Set-Cookie")
            var newRefreshToken: String? = null

            for (header in setCookieHeaders) {
                header.split(";").forEach { part ->
                    val trimmed = part.trim()
                    if (trimmed.startsWith("refreshToken=")) {
                        newRefreshToken = trimmed.removePrefix("refreshToken=")
                    }
                }
            }

            if (!newRefreshToken.isNullOrEmpty()) {
                Log.d("AuthInterceptor", "RefreshToken updated from Set-Cookie")
                TokenManager.saveTokens(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken!!
                )
            } else {
                TokenManager.saveTokens(
                    accessToken = newAccessToken,
                    refreshToken = currentRefreshToken
                )
            }

            Log.d("AuthInterceptor", "AccessToken refreshed successfully.")
            newAccessToken

        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Exception during token refresh: ${e.message}", e)
            null
        }
    }
}
