package com.example.iot_air_quality_android.ui.common

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import java.util.Calendar

object UiPickerHelper {

    /** 📅 날짜 선택 다이얼로그 */
    fun showDatePicker(context: Context, targetView: TextView) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                targetView.text = formatted
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val minDate = Calendar.getInstance().apply { set(1900, 0, 1) }.timeInMillis
        val maxDate = System.currentTimeMillis()
        datePicker.datePicker.minDate = minDate
        datePicker.datePicker.maxDate = maxDate
        datePicker.show()
    }

    /** 📋 메뉴 선택 팝업 */
    fun showPopupMenu(
        context: Context,
        anchorView: View,
        items: List<String>,
        onSelected: (String) -> Unit
    ) {
        val popup = PopupMenu(context, anchorView)
        items.forEach { popup.menu.add(it) }
        popup.setOnMenuItemClickListener { item ->
            onSelected(item.title.toString())
            true
        }
        popup.show()
    }
}
