// ui/history/HistoryAdapter.kt
package com.example.iot_air_quality_android.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.data.model.history.HistoryRecordUiModel
import com.example.iot_air_quality_android.data.model.history.MetricStatus

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val items = mutableListOf<HistoryRecordUiModel>()

    fun submitList(newItems: List<HistoryRecordUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_record, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvMeasuredAt: TextView = itemView.findViewById(R.id.tvMeasuredAt)

        private val metricViews: List<CardView> = listOf(
            itemView.findViewById(R.id.itemPm25),
            itemView.findViewById(R.id.itemPm10),
            itemView.findViewById(R.id.itemTemperature),
            itemView.findViewById(R.id.itemHumidity),
            itemView.findViewById(R.id.itemCo2),
            itemView.findViewById(R.id.itemVoc)
        )

        fun bind(item: HistoryRecordUiModel) {
            tvMeasuredAt.text = item.measuredAtText

            val metrics = listOf(
                item.pm25,
                item.pm10,
                item.temperature,
                item.humidity,
                item.co2,
                item.voc
            )

            metricViews.zip(metrics).forEach { (viewGroup, metric) ->
                val title = viewGroup.findViewById<TextView>(R.id.tvMetricTitle)
                val value = viewGroup.findViewById<TextView>(R.id.tvMetricValue)
                val dot = viewGroup.findViewById<View>(R.id.viewStatusDot)

                if (metric == null) {
                    viewGroup.visibility = View.INVISIBLE
                } else {
                    viewGroup.visibility = View.VISIBLE
                    title.text = metric.title
                    value.text = metric.valueText

                    dot.setBackgroundColor(
                        ContextCompat.getColor(
                            dot.context,
                            when (metric.status) {
                                MetricStatus.GOOD      -> R.color.level_good
                                MetricStatus.MODERATE  -> R.color.level_moderate
                                MetricStatus.BAD       -> R.color.level_bad
                                MetricStatus.VERY_BAD  -> R.color.level_verybad
                                MetricStatus.UNKNOWN   -> android.R.color.darker_gray
                            }
                        )
                    )
                }
            }
        }
    }
}
