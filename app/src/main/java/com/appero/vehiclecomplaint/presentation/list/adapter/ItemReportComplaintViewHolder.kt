package com.appero.vehiclecomplaint.presentation.list.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.ItemComplaintBinding
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.utilities.date_time.DateTimeHelper
import com.bumptech.glide.Glide

class ItemReportComplaintViewHolder(private val binding: ItemComplaintBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(report: Report) {
        with(itemView) {
            Glide.with(context)
                .load(report.photo.toString())
                .fitCenter()
                .error(R.drawable.img_not_found)
                .into(binding.imgPreview)

            binding.apply {
                txtReportId.text = report.reportId
                txtReportedName.text = report.createdBy
                txtStatus.text = report.reportStatus ?: "Gagal"
                txtStatus.backgroundTintList = if (report.reportStatus == "Terkirim") ColorStateList.valueOf(resources.getColor(R.color.green, null)) else ColorStateList.valueOf(resources.getColor(R.color.red, null))
                txtVehicleName.text = report.vehicleName
                txtLegacyNumber.text = report.vehicleLicenseNumber
                txtNotes.text = report.note
                txtDateTime.text = setTimeFormatted(report.createdAt.toString())
            }
        }
    }

    private fun setTimeFormatted(timestamp: String): String {
        val dateTimeHelper = DateTimeHelper()
        val parse = dateTimeHelper.parseDateTime(timestamp)
        return dateTimeHelper.formatDateTime(parse)
    }

    companion object {
        fun create(view: ViewGroup): ItemReportComplaintViewHolder {
            val inflater = LayoutInflater.from(view.context)
            val binding = ItemComplaintBinding.inflate(inflater, view, false)
            return ItemReportComplaintViewHolder(binding)
        }
    }

}