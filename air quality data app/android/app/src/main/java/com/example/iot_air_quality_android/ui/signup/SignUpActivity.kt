package com.example.iot_air_quality_android.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.data.model.request.SignUpRequest
import com.example.iot_air_quality_android.data.model.type.BloodType
import com.example.iot_air_quality_android.ui.common.UiPickerHelper
import com.example.iot_air_quality_android.ui.main.MainActivity
import com.example.iot_air_quality_android.util.TokenManager
import com.example.iot_air_quality_android.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var textBirthDate: TextView
    private lateinit var textGender: TextView
    private lateinit var textBloodType: TextView
    private lateinit var textCountryCode: TextView

    private lateinit var inputHeight: EditText
    private lateinit var inputWeight: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPhone: EditText

    private lateinit var buttonReset: Button
    private lateinit var buttonNext: Button

    private var googleUserName: String? = null
    private var googleUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupPickers()
        setupListeners()
        observeViewModel()

        googleUserName = intent.getStringExtra("userName")
        googleUserEmail = intent.getStringExtra("userEmail")
        googleUserEmail?.let { inputEmail.setText(it) }
        Log.d("SignUpActivity", "googleUserName = $googleUserName")
    }

    private fun initViews() {
        textBirthDate = findViewById(R.id.text_birth_date)
        textGender = findViewById(R.id.text_gender)
        textBloodType = findViewById(R.id.text_blood_type)
        textCountryCode = findViewById(R.id.text_country_code)

        inputHeight = findViewById(R.id.input_height)
        inputWeight = findViewById(R.id.input_weight)
        inputEmail = findViewById(R.id.input_email)
        inputPhone = findViewById(R.id.input_phone)

        buttonReset = findViewById(R.id.button_reset)
        buttonNext = findViewById(R.id.button_next)
    }

    private fun setupPickers() {
        // 📅 생년월일
        textBirthDate.setOnClickListener {
            UiPickerHelper.showDatePicker(this, textBirthDate)
        }

        textGender.setOnClickListener {
            UiPickerHelper.showPopupMenu(this, it, listOf("MALE", "FEMALE", "OTHER")) { selected ->
                textGender.text = selected
            }
        }

        textBloodType.setOnClickListener {
            UiPickerHelper.showPopupMenu(this, it, BloodType.displayList()) { selected ->
                textBloodType.text = selected
            }
        }

        // 🌍 국가 코드
        textCountryCode.setOnClickListener {
            UiPickerHelper.showPopupMenu(this, it, listOf("US", "KR")) { selected ->
                textCountryCode.text = selected
            }
        }
    }

    private fun setupListeners() {
        buttonReset.setOnClickListener {
            textBirthDate.text = "Select Birth Date"
            textGender.text = "Select Gender"
            textBloodType.text = "Select Blood Type"
            textCountryCode.text = "US"
            inputHeight.text.clear()
            inputWeight.text.clear()
            inputEmail.text.clear()
            inputPhone.text.clear()
            buttonNext.isEnabled = false
        }

        buttonNext.setOnClickListener {
            val selectedBloodType = BloodType.fromDisplay(textBloodType.text.toString()).serverValue

            val request = SignUpRequest(
                name = googleUserName ?: "Unknown",
                email = inputEmail.text.toString(),
                gender = textGender.text.toString(),
                phoneNumber = inputPhone.text.toString(),
                nationalCode = textCountryCode.text.toString(),
                dateOfBirth = textBirthDate.text.toString(),
                bloodType = selectedBloodType,
                height = inputHeight.text.toString().toDoubleOrNull() ?: 0.0,
                weight = inputWeight.text.toString().toDoubleOrNull() ?: 0.0
            )

            lifecycleScope.launch {
                viewModel.signUp(request)
            }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                buttonNext.isEnabled = isFormComplete()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        listOf(textBirthDate, textGender, textBloodType, textCountryCode, inputHeight, inputWeight, inputEmail, inputPhone).forEach {
            it.addTextChangedListener(watcher)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.signUpState.collect { state ->
                when (state) {
                    is SignUpViewModel.ResultState.Loading -> {
                        Toast.makeText(this@SignUpActivity, "회원정보 저장 중...", Toast.LENGTH_SHORT).show()
                    }
                    is SignUpViewModel.ResultState.Success -> {
                        val data = state.data
                        if (data != null) {
                            TokenManager.saveTokens(data.accessToken, data.refreshToken)
                            Toast.makeText(this@SignUpActivity, "회원가입 완료!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    is SignUpViewModel.ResultState.Error -> {
                        Toast.makeText(this@SignUpActivity, "오류: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun isFormComplete(): Boolean {
        val invalidTexts = listOf("Select Birth Date", "Select Gender", "Select Blood Type")

        val isTextFieldsValid = !invalidTexts.contains(textBirthDate.text.toString()) &&
                !invalidTexts.contains(textGender.text.toString()) &&
                !invalidTexts.contains(textBloodType.text.toString())

        return isTextFieldsValid &&
                inputHeight.text.isNotEmpty() &&
                inputWeight.text.isNotEmpty() &&
                inputEmail.text.isNotEmpty() &&
                inputPhone.text.isNotEmpty()
    }
}
