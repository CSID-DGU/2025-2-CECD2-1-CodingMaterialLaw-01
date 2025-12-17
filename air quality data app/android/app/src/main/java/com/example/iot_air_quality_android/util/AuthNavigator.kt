package com.example.iot_air_quality_android.util

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.iot_air_quality_android.ui.login.LoginActivity
import com.example.iot_air_quality_android.ui.main.MainActivity
import com.example.iot_air_quality_android.ui.signup.TermsAgreementActivity

object AuthNavigator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun forceLogoutToLogin(showToast: Boolean = true) {
        if (!::appContext.isInitialized) {
            Log.e("AuthNavigator", "appContext is not initialized")
            return
        }

        Handler(Looper.getMainLooper()).post {
            if (showToast) {
                Toast.makeText(
                    appContext,
                    "Your session has expired. Please log in again.",
                    Toast.LENGTH_LONG
                ).show()
            }

            // 토큰 정리
            TokenManager.clearTokens()

            // 로그인 화면으로 스택 리셋 + 이동
            val intent = Intent(appContext, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            appContext.startActivity(intent)
            Log.d("AuthNavigator", "Force navigating to LoginActivity")
        }
    }

    fun navigateAfterAutoLogin(context: Context): Boolean {
        val token = TokenManager.getAccessToken()

        if (token.isNullOrEmpty()) {
            Log.d("AuthNavigator", "No token found → Stay on LoginActivity")
            return false
        }

        val role = JwtUtil.parseRoleFromToken(token)
        Log.d("AuthNavigator", "Auto login detected → role: $role")

        when (role) {
            "GUEST", "HD_USER", "PM" -> {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    putExtra("userRole", role)
                }
                context.startActivity(intent)
                Log.d("AuthNavigator", "Navigating to LoginActivity for $role")
                TokenManager.clearTokens();
                return false
            }

            "AQD_USER", "BOTH_USER" -> {
                context.startActivity(Intent(context, MainActivity::class.java))
                Log.d("AuthNavigator", "Navigating to MainActivity for $role")
                return true
            }

            else -> {
                Log.e("AuthNavigator", "Invalid or null role → clearing tokens")
                TokenManager.clearTokens()
                return false
            }
        }
    }

    // ✅ 여기에 이름, 이메일 인자 추가
    fun navigateAfterLogin(
        context: Context,
        role: String?,
        userName: String?,
        userEmail: String?
    ): Boolean {
        if (role.isNullOrEmpty()) {
            Log.e("AuthNavigator", "Role is null → stay on login screen")
            return false
        }

        when (role) {
            "GUEST", "HD_USER", "PM" -> {
                val intent = Intent(context, TermsAgreementActivity::class.java).apply {
                    putExtra("userRole", role)
                    putExtra("userName", userName)
                    putExtra("userEmail", userEmail)
                }
                context.startActivity(intent)
                Log.d("AuthNavigator", "Navigating to TermsAgreementActivity for $role")
                return true
            }

            "AQD_USER", "BOTH_USER" -> {
                context.startActivity(Intent(context, MainActivity::class.java))
                Log.d("AuthNavigator", "Navigating to MainActivity for $role")
                return true
            }

            else -> {
                Log.e("AuthNavigator", "Invalid role ($role) → stay on login screen")
                return false
            }
        }
    }
}
