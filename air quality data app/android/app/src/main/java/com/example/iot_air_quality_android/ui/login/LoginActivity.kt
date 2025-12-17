package com.example.iot_air_quality_android.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.*
import androidx.lifecycle.lifecycleScope
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.util.AuthNavigator
import com.example.iot_air_quality_android.util.LocationUtil
import com.example.iot_air_quality_android.viewmodel.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import kotlinx.coroutines.launch
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var credentialManager: CredentialManager

    private lateinit var googleLoginButton: ImageButton
    private lateinit var loginProgressBar: ProgressBar
    private lateinit var loginErrorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        credentialManager = CredentialManager.create(this)

        googleLoginButton = findViewById(R.id.btn_google_login)
        loginProgressBar = findViewById(R.id.loginProgressBar)
        loginErrorTextView = findViewById(R.id.loginErrorTextView)

        setLoading(false)
        clearLoginError()

        if (!LocationUtil.checkAndRequestPermissions(this)) {
            finishAffinity()
            return
        }

        // ✅ 자동 로그인 분기
        if (AuthNavigator.navigateAfterAutoLogin(this)) {
            finish()
            return
        }

        googleLoginButton.setOnClickListener {
            clearLoginError()
            setLoading(true)
            lifecycleScope.launch { signInWithGoogle() }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        googleLoginButton.isEnabled = !isLoading
        googleLoginButton.alpha = if (isLoading) 0.5f else 1.0f  // 시각적으로 비활성 느낌
    }

    private fun showLoginError(message: String) {
        loginErrorTextView.text = message
        loginErrorTextView.visibility = View.VISIBLE
    }

    private fun clearLoginError() {
        loginErrorTextView.text = ""
        loginErrorTextView.visibility = View.GONE
    }

    private suspend fun signInWithGoogle() {
        val nonce = UUID.randomUUID().toString()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("1019619556439-7ia5ml9b0jivm14r19d6u86r259kfsnq.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        try {
            val result = credentialManager.getCredential(this, request)
            val credential = result.credential as? CustomCredential
                ?: run {
                    setLoading(false)
                    showLoginError("Unable to retrieve your Google credentials. Please try again.")
                    return
                }
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleCredential.idToken
            val userEmail = googleCredential.id ?: ""
            // TODO : 사용자 이름 가져오기
            val userName = userEmail.substringBefore("@").ifBlank {
                "User_${System.currentTimeMillis()}"
            }

            // ✅ ViewModel에 로그인 요청
            viewModel.loginWithGoogle(idToken) { role ->
                setLoading(false)

                if (role != null) {
                    clearLoginError()
                    Log.d("LoginActivity", "User Role: $role")

                    // ✅ 로그인 성공 후 화면 이동도 AuthNavigator에 위임
                    if (AuthNavigator.navigateAfterLogin(this, role, userName, userEmail)) {
                        finish()
                    } else {
                        Log.e("LoginActivity", "❌ Invalid or unknown role after login")
                        showLoginError("We couldn't verify your account. Please try again later.")
                    }
                } else {
                    Log.e("LoginActivity", "❌ Login failed: role is null")
                    showLoginError("Sign-in failed. Please try again.")
                }
            }

        } catch (e: GetCredentialCancellationException) {
            // 사용자가 구글 로그인 UI에서 '뒤로가기/취소'한 경우
            setLoading(false)
            Log.e("LoginActivity", "Google Sign-In canceled: ${e.message}")
            showLoginError("Sign-in was canceled.")

        } catch (e: Exception) {
            setLoading(false)
            Log.e("LoginActivity", "Google Sign-In failed: ${e.message}", e)
            showLoginError("Sign-in failed. Please check your network status.")
        }
    }
}
