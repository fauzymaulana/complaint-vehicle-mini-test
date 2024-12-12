package com.appero.vehiclecomplaint.presentation.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.utilities.OnClickListenerAdapter

class ReportComplaintAdapter(
    private val onClick: OnClickListenerAdapter<Report>
): ListAdapter<Report, RecyclerView.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemReportComplaintViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ItemReportComplaintViewHolder).bind(item)
        holder.itemView.setOnClickListener {
            onClick.onClick(item)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(
                oldItem: Report,
                newItem: Report
            ): Boolean = oldItem.reportId == newItem.reportId

            override fun areContentsTheSame(
                oldItem: Report,
                newItem: Report
            ): Boolean = oldItem == newItem

        }
    }
}