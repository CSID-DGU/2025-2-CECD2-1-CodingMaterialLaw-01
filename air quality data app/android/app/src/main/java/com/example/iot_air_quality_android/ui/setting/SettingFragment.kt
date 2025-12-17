package com.example.iot_air_quality_android.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.ui.login.LoginActivity
import com.example.iot_air_quality_android.util.TokenManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.lifecycle.lifecycleScope
import com.example.iot_air_quality_android.data.api.RetrofitInstance
import com.example.iot_air_quality_android.data.model.response.ProjectItemDto
import kotlinx.coroutines.launch
import com.example.iot_air_quality_android.data.model.response.ProjectInfoResponse
import com.example.iot_air_quality_android.data.model.response.ProjectBoolField
import com.example.iot_air_quality_android.data.model.response.personalFields
import com.example.iot_air_quality_android.data.model.response.healthFields
import com.example.iot_air_quality_android.data.model.response.airFields
import retrofit2.HttpException


class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val api = RetrofitInstance.api

    private lateinit var Logout: TextView
    private lateinit var Logo: ImageView

    private lateinit var SelectedProject: TextView
    private lateinit var btnProjectInfo: ImageButton
    private lateinit var btnViewDataOnWeb: Button
    private lateinit var btnInputMetaOnWeb: Button

    private lateinit var swPrivacyPolicy: SwitchCompat
    private lateinit var swTermsOfService: SwitchCompat
    private lateinit var swHealthConsent: SwitchCompat
    private lateinit var swAirDataConsent: SwitchCompat
    private lateinit var swLocationConsent: SwitchCompat

    private lateinit var PrivacyPolicyView: TextView
    private lateinit var TermsOfServiceView: TextView
    private lateinit var HealthConsentView: TextView
    private lateinit var AirDataConsentView: TextView
    private lateinit var LocationConsentView: TextView

    private lateinit var btnReset: Button
    private lateinit var btnRegister: Button

    private var projects: List<ProjectItemDto> = emptyList()
    private var selectedProject: ProjectItemDto? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupListeners()
        loadProjects()
    }

    private fun bindViews(root: View) {
        Logout = root.findViewById(R.id.Logout)
        Logo = root.findViewById(R.id.Logo)

        SelectedProject = root.findViewById(R.id.SelectedProject)
        btnProjectInfo = root.findViewById(R.id.btnProjectInfo)
        btnViewDataOnWeb = root.findViewById(R.id.btnViewDataOnWeb)
        btnInputMetaOnWeb = root.findViewById(R.id.btnInputMetaOnWeb)

        swPrivacyPolicy = root.findViewById(R.id.swPrivacyPolicy)
        swTermsOfService = root.findViewById(R.id.swTermsOfService)
        swHealthConsent = root.findViewById(R.id.swHealthConsent)
        swAirDataConsent = root.findViewById(R.id.swAirDataConsent)
        swLocationConsent = root.findViewById(R.id.swLocationConsent)

        PrivacyPolicyView = root.findViewById(R.id.tvPrivacyPolicyView)
        TermsOfServiceView = root.findViewById(R.id.tvTermsOfServiceView)
        HealthConsentView = root.findViewById(R.id.tvHealthConsentView)
        AirDataConsentView = root.findViewById(R.id.tvAirDataConsentView)
        LocationConsentView = root.findViewById(R.id.tvLocationConsentView)

        btnReset = root.findViewById(R.id.btnReset)
        btnRegister = root.findViewById(R.id.btnRegister)
    }

    private fun setupListeners() {
        // 로그아웃
        Logout.setOnClickListener {
            showLogoutConfirmDialog()
        }

        // 프로젝트 선택
        SelectedProject.setOnClickListener {
            showProjectSelectionDialog()
        }

        // 프로젝트 info
        btnProjectInfo.setOnClickListener {
            selectedProject?.let { project ->
                loadProjectDetail(project)
            }
        }

        // Web 버튼들
        btnViewDataOnWeb.setOnClickListener {
            openUrl("https://kibana.monodatum.io")
        }

        btnInputMetaOnWeb.setOnClickListener {
            openUrl("https://monodatum.io/metadata/submit")
        }

        // 약관 View 클릭 -> 상세조회 (API 호출 후 다이얼로그 or 화면)
        PrivacyPolicyView.setOnClickListener { showTermsDetail(TermsType.PRIVACY) }
        TermsOfServiceView.setOnClickListener { showTermsDetail(TermsType.SERVICE) }
        HealthConsentView.setOnClickListener { showTermsDetail(TermsType.HEALTH) }
        AirDataConsentView.setOnClickListener { showTermsDetail(TermsType.AIR) }
        LocationConsentView.setOnClickListener { showTermsDetail(TermsType.LOCATION) }

        // 스위치 상태 바뀔 때마다 Register 버튼 활성화 여부 갱신
        val switches = listOf(
            swPrivacyPolicy,
            swTermsOfService,
            swHealthConsent,
            swAirDataConsent,
            swLocationConsent
        )

        switches.forEach { sw ->
            sw.setOnCheckedChangeListener { _, _ ->
                updateRegisterButtonState()
            }
        }

        // Reset 버튼: 스위치 모두 OFF
        btnReset.setOnClickListener {
            switches.forEach { it.isChecked = false }
            updateRegisterButtonState()
        }

        // Register 버튼: 프로젝트 등록 API
        btnRegister.setOnClickListener {
            selectedProject?.let { project ->
                registerToProject(project)
            }
        }
    }

    private fun updateRegisterButtonState() {
        val allChecked =
            swPrivacyPolicy.isChecked &&
            swTermsOfService.isChecked &&
            swHealthConsent.isChecked &&
            swAirDataConsent.isChecked &&
            swLocationConsent.isChecked

        btnRegister.isEnabled = allChecked
    }

    // ----- 프로젝트 로딩 / 선택 -----
    private fun loadProjects() {
        lifecycleScope.launch {
            try {
                val wrapper = api.getProjects()
                if (!wrapper.success) return@launch

                projects = wrapper.data?.projectList ?: emptyList()

                if (projects.isNotEmpty()) {
                    setSelectedProject(projects.first())
                } else {
                    SelectedProject.text = "No projects"
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setSelectedProject(project: ProjectItemDto) {
        selectedProject = project
        SelectedProject.text = project.projectTitle

        swPrivacyPolicy.isChecked = false
        swTermsOfService.isChecked = false
        swHealthConsent.isChecked = false
        swAirDataConsent.isChecked = false
        swLocationConsent.isChecked = false

        updateRegisterButtonState()
    }

    private fun showProjectSelectionDialog() {
        if (projects.isEmpty()) return

        val names = projects.map { it.projectTitle }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Project")
            .setItems(names) { _, which ->
                setSelectedProject(projects[which])
            }
            .show()
    }

    // 프로젝트 상세 정보 로딩 (API 붙일 자리)
    private fun loadProjectDetail(project: ProjectItemDto) {
        lifecycleScope.launch {
            try {
                // AuthInterceptor가 Authorization 붙여줌
                val wrapper = api.getProjectInfo(project.projectId)

                if (!wrapper.success) {
                    // TODO: wrapper.error 사용해서 토스트 띄우고 싶으면 여기서
                    return@launch
                }

                val detail = wrapper.data ?: return@launch
                showProjectDetailBottomSheet(detail)

            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: 네트워크 에러 토스트 등
            }
        }
    }

    private fun showProjectDetailBottomSheet(detail: ProjectInfoResponse) {
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.bottom_sheet_project_detail, null)

        dialog.setContentView(view)

        // ===== 메타 데이터 바인딩 =====
        view.findViewById<TextView>(R.id.tvMetaTitleValue).text = detail.projectTitle
        view.findViewById<TextView>(R.id.tvMetaPmEmailValue).text = detail.pmEmail
        view.findViewById<TextView>(R.id.tvMetaParticipantsValue).text = detail.participant.toString()
        view.findViewById<TextView>(R.id.tvMetaDescriptionValue).text = detail.description
        view.findViewById<TextView>(R.id.tvMetaTypeValue).text = detail.projectType
        view.findViewById<TextView>(R.id.tvMetaStartDateValue).text = detail.startDate
        view.findViewById<TextView>(R.id.tvMetaEndDateValue).text = detail.endDate
        view.findViewById<TextView>(R.id.tvMetaCreatedAtValue).text = detail.createdAt

        // ===== Bool 필드들 체크박스로 동적 추가 =====
        val llPersonal = view.findViewById<LinearLayout>(R.id.llPersonalFields)
        val llHealth = view.findViewById<LinearLayout>(R.id.llHealthFields)
        val llAir = view.findViewById<LinearLayout>(R.id.llAirFields)

        fun addBoolFields(container: LinearLayout, fields: List<ProjectBoolField>) {
            container.removeAllViews()
            fields.forEach { field ->
                val cb = CheckBox(requireContext()).apply {
                    text = field.label
                    isChecked = field.value
                    isEnabled = false  // 읽기만
                    setPadding(0, 4, 0, 4)
                }
                container.addView(cb)
            }
        }

        addBoolFields(llPersonal, detail.personalFields())
        addBoolFields(llHealth, detail.healthFields())
        addBoolFields(llAir, detail.airFields())

        dialog.show()
    }

    // ----- 약관 상세 -----
    private enum class TermsType {
        PRIVACY, SERVICE, HEALTH, AIR, LOCATION
    }

    private fun showTermsDetail(type: TermsType) {
        val project = selectedProject
        if (project == null) {
            Toast.makeText(requireContext(), "Please select a project.", Toast.LENGTH_SHORT).show()
            return
        }

        val title = when (type) {
            TermsType.PRIVACY -> "Privacy Policy"
            TermsType.SERVICE -> "Terms of Service"
            TermsType.HEALTH -> "Health Data Consent"
            TermsType.AIR -> "Air Data Consent"
            TermsType.LOCATION -> "Location Data Consent"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wrapper = when (type) {
                    TermsType.PRIVACY ->
                        api.getPrivacyPolicyTerms(project.projectId)

                    TermsType.SERVICE ->
                        api.getTermsOfService(project.projectId)

                    TermsType.HEALTH ->
                        api.getHealthDataConsent(project.projectId)

                    TermsType.LOCATION ->
                        api.getLocationDataConsent(project.projectId)

                    TermsType.AIR ->
                        api.getAirDataConsent(project.projectId)
                }

                if (!wrapper.success) {
                    val msg = wrapper.error?.message ?: "Failed to load the terms."
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val content = wrapper.data?.content ?: "(No content.)"

                AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()

            } catch (e: HttpException) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Server Error : (${e.code()})",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Network error - Failed to load the terms. Try again in a moment.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ----- 프로젝트 등록 -----
    private fun registerToProject(project: ProjectItemDto) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wrapper = api.participateInProject(project.projectId)

                if (wrapper.success) {
                    Toast.makeText(
                        requireContext(),
                        "You have joined the project.",
                        Toast.LENGTH_SHORT
                    ).show()

                    btnRegister.isEnabled = false

                } else {
                    val error = wrapper.error
                    val msg = error?.message ?: "Something went wrong while joining the project."
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    Toast.makeText(
                        requireContext(),
                        "You’ve already joined this project.",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnRegister.isEnabled = false
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Server Error : (${e.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Network error - Registration wasn’t completed. Try again in a moment.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ----- Logout -----
    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 서버 로그아웃 시도
                val response = api.logout()

                if (!response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Logout on the server failed, but you’ll still be logged out from the app.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Successfully logged out.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                TokenManager.clearTokens()

                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Yes") { _, _ ->
                logout()
            }
            .setPositiveButton("No", null)
            .show()
    }

    private fun openUrl(url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
