package com.example.iot_air_quality_android.ui.signup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.data.model.response.TermItem
import com.example.iot_air_quality_android.ui.main.MainActivity
import com.example.iot_air_quality_android.util.TokenManager
import com.example.iot_air_quality_android.viewmodel.TermsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TermsAgreementActivity : AppCompatActivity() {

    private val viewModel: TermsViewModel by viewModels()

    private lateinit var layoutTermsContainer: LinearLayout
    private lateinit var buttonNext: Button
    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_agreement)

        layoutTermsContainer = findViewById(R.id.layout_terms_container)
        buttonNext = findViewById(R.id.button_next)
        buttonReset = findViewById(R.id.button_reset)

        // 로그인 시 전달된 role
        viewModel.userRole = intent.getStringExtra("userRole") ?: "GUEST"

        observeTerms()
        setupButtons()
    }

    private fun observeTerms() {
        lifecycleScope.launch {
            viewModel.terms.collectLatest { list ->
                Log.d("TermsAgreementActivity", "terms list size = ${list.size}")
                layoutTermsContainer.removeAllViews()
                list.forEach { termUi ->
                    Log.d("TermsAgreementActivity", "Adding term: ${termUi.term.title}")
                    addTermItemView(termUi.term, termUi.isAgreed)
                }
                buttonNext.isEnabled = viewModel.allAgreed
            }
        }
    }

    private fun addTermItemView(term: TermItem, isAgreed: Boolean) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 8, 8, 8)
            weightSum = 10f
        }

        val titleView = TextView(this).apply {
            text = term.title
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6f)
        }

        val viewButton = Button(this).apply {
            text = "View"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
            setOnClickListener { showTermDetail(term) }
        }

        val toggle = Switch(this).apply {
            isChecked = isAgreed
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
            setOnCheckedChangeListener { _, checked ->
                viewModel.toggleTerm(term.type, checked)
                buttonNext.isEnabled = viewModel.allAgreed
            }
        }

        itemLayout.addView(titleView)
        itemLayout.addView(viewButton)
        itemLayout.addView(toggle)
        layoutTermsContainer.addView(itemLayout)
    }

    private fun setupButtons() {
        buttonReset.setOnClickListener {
            viewModel.resetAll()
        }

        buttonNext.setOnClickListener {
            handleNext()
        }
    }

    private fun showTermDetail(term: TermItem) {
        AlertDialog.Builder(this)
            .setTitle(term.title)
            .setMessage(term.content)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun handleNext() {
        when (viewModel.userRole) {
            "GUEST" -> {
                val intent = Intent(this, SignUpActivity::class.java).apply {
                    putExtra("userRole", viewModel.userRole)
                    putExtra("userName", getIntent().getStringExtra("userName"))
                    putExtra("userEmail", getIntent().getStringExtra("userEmail"))
                }
                startActivity(intent)
                finish()
            }

            "HD_USER", "PM" -> {
                lifecycleScope.launch {
                    try {
                        // ✅ 서버로 Role 업데이트 요청
                        val tokenResponse = viewModel.updateUserRole()

                        TokenManager.saveTokens(
                            accessToken = tokenResponse.accessToken,
                            refreshToken = tokenResponse.refreshToken
                        )

                        val intent = Intent(this@TermsAgreementActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@TermsAgreementActivity, "Role update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
