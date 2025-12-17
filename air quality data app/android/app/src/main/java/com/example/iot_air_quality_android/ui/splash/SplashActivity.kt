// SplashActivity.kt
package com.example.iot_air_quality_android.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.iot_air_quality_android.ui.login.LoginActivity
import com.example.iot_air_quality_android.util.AuthNavigator
import com.example.iot_air_quality_android.util.JwtUtil
import com.example.iot_air_quality_android.util.TokenManager

class SplashActivity : AppCompatActivity() {

    private val permissionRequestCode = 1001

    private val requiredPermissions: Array<String> by lazy {
        val list = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list += Manifest.permission.BLUETOOTH_SCAN
            list += Manifest.permission.BLUETOOTH_CONNECT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list += Manifest.permission.POST_NOTIFICATIONS
        }

        list.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasAllPermissions()) {
            requestPermissions(requiredPermissions, permissionRequestCode)
        } else {
            goNext()
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                goNext()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("App cannot work properly without required permissions.")
                    .setPositiveButton("Exit") { _, _ -> finishAffinity() }
                    .show()
            }
        }
    }

    /**
     * ✅ 여기서는 refresh 호출 안 함
     *  - refreshToken, accessToken 유무로만 화면 분기
     *  - 실제 API 호출에서 토큰 만료시 → AuthInterceptor가 알아서 refresh
     */
    private fun goNext() {
        val refreshToken = TokenManager.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            Log.d("SplashActivity", "No refreshToken → go LoginActivity")
            goLogin()
            return
        }

        val isRtExpired = JwtUtil.isJwtExpired(refreshToken, skewSeconds = 30L)
        if (isRtExpired) {
            Log.d("SplashActivity", "refreshToken expired → clear tokens & go LoginActivity")
            TokenManager.clearTokens()
            goLogin()
            return
        }

        Log.d("SplashActivity", "refreshToken valid → navigate by role")

        val moved = AuthNavigator.navigateAfterAutoLogin(this)

        if (!moved) {
            Log.d("SplashActivity", "navigateAfterAutoLogin returned false → fallback to LoginActivity")
            goLogin()
        }

        finish()
    }

    private fun goLogin() {
        Log.d("SplashActivity", "goLogin() called")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
