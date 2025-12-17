// ui/history/HistoryFragment.kt
package com.example.iot_air_quality_android.ui.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iot_air_quality_android.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var spinnerProject: Spinner
    private lateinit var cardSelectDate: CardView
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnOpenCalendar: ImageButton
    private lateinit var rvHistory: RecyclerView
    private lateinit var cardEmptyState: CardView
    private lateinit var cardNoResult: CardView

    private val historyAdapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        bindViews(view)
        setupSpinnerListener()
        setupDatePicker()
        setupRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                render(state)
            }
        }
    }

    private fun bindViews(view: View) {
        spinnerProject = view.findViewById(R.id.spinnerProject)
        cardSelectDate = view.findViewById(R.id.cardSelectDate)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        btnOpenCalendar = view.findViewById(R.id.btnOpenCalendar)
        rvHistory = view.findViewById(R.id.rvHistory)
        cardEmptyState = view.findViewById(R.id.cardEmptyState)
        cardNoResult = view.findViewById(R.id.cardNoResult)
    }

    private fun setupSpinnerListener() {
        spinnerProject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val state = viewModel.uiState.value
                val projects = state.projects
                if (projects.isEmpty()) return

                if (position == 0) {
                    viewModel.selectProject(null)
                } else {
                    val projectId = projects[position - 1].projectId
                    if (projectId != state.selectedProjectId) {
                        viewModel.selectProject(projectId)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupDatePicker() {
        val openPicker: () -> Unit = {
            val base = viewModel.uiState.value.selectedDate ?: LocalDate.now()

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    viewModel.selectDate(LocalDate.of(year, month + 1, dayOfMonth))
                },
                base.year,
                base.monthValue - 1,
                base.dayOfMonth
            ).show()
        }

        btnOpenCalendar.setOnClickListener { openPicker() }
        cardSelectDate.setOnClickListener { openPicker() }
    }

    private fun setupRecyclerView() {
        rvHistory.adapter = historyAdapter
        rvHistory.layoutManager = LinearLayoutManager(requireContext())

        rvHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return

                val lm = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = lm.findLastVisibleItemPosition()
                val total = lm.itemCount

                if (lastVisible >= total - 5) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun render(state: HistoryUiState) {
        // 프로젝트 스피너 (프로젝트 목록이 바뀐 경우에만 세팅)
        if (state.projects.isNotEmpty() && spinnerProject.adapter == null) {
            val labels = listOf("Select Project") + state.projects.map { it.title }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                labels
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spinnerProject.adapter = adapter
        }

        // 선택된 프로젝트 인덱스 복원
        state.selectedProjectId?.let { selectedId ->
            val idx = state.projects.indexOfFirst { it.projectId == selectedId }
            if (idx >= 0 && spinnerProject.selectedItemPosition != idx + 1) {
                spinnerProject.setSelection(idx + 1, false)
            }
        }.also {
            if (state.selectedProjectId == null && spinnerProject.selectedItemPosition != 0) {
                spinnerProject.setSelection(0, false)
            }
        }

        // 날짜 표시
        tvSelectedDate.text = state.selectedDate?.toString() ?: "Select Date"

        // 리스트 데이터
        historyAdapter.submitList(state.records)

        // 상태별 UI 가시성
        val hasSelection = state.selectedProjectId != null && state.selectedDate != null
        val hasRecords = state.records.isNotEmpty()

        when {
            !hasSelection -> {
                // 아무것도 선택 안 됨
                cardEmptyState.visibility = View.VISIBLE
                cardNoResult.visibility = View.GONE
                rvHistory.visibility = View.GONE
            }
            hasSelection && hasRecords -> {
                // 데이터 있음
                cardEmptyState.visibility = View.GONE
                cardNoResult.visibility = View.GONE
                rvHistory.visibility = View.VISIBLE
            }
            hasSelection && !hasRecords && !state.isLoading -> {
                // 선택은 했는데, 로딩 끝 + 결과 없음
                cardEmptyState.visibility = View.GONE
                cardNoResult.visibility = View.VISIBLE
                rvHistory.visibility = View.GONE
            }
            else -> {
                // 로딩 중인 상태(스피너/스켈레톤 넣고 싶으면 여기)
                cardEmptyState.visibility = View.GONE
                cardNoResult.visibility = View.GONE
                rvHistory.visibility = if (hasRecords) View.VISIBLE else View.GONE
            }
        }
    }
}
