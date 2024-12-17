package com.appero.vehiclecomplaint.utilities.pop_up

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FormReportDialogBinding
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class GeneralDialog(private val context: Context) {
    val formDialog by lazy { FormReportDialogBinding.inflate(LayoutInflater.from(context)) }
    private var pickImageLauncher: ActivityResultLauncher<String>? = null

    fun setFormDialog(
        dateTime: String,
        listVehicle: List<Vehicle>,
        cancelable: Boolean = false,
        activityResultLauncher: ActivityResultLauncher<String>,
        callback: (MaterialButton, MaterialButton, MaterialAutoCompleteTextView, ImageView,AlertDialog) -> Unit
    ) {
        formDialog.apply {
            txtDateTime.text = dateTime
            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, listVehicle.map { it.type })
            vehicleDropdown.setAdapter(adapter)

            val dialog = MaterialAlertDialogBuilder(context, R.style.DialogOverlayTheme)
                .setView(root)
                .setBackgroundInsetStart(16)
                .setBackgroundInsetEnd(16)
                .setCancelable(cancelable)
                .show()

            dialog.window?.setBackgroundDrawableResource(R.drawable.background_bottom_rounded_10_radius)

            val marginInPx = (21 * context.resources.displayMetrics.density).toInt()
            dialog?.window?.apply {
                setLayout(
                    context.resources.displayMetrics.widthPixels - 2 * marginInPx,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }

            pickImageLauncher = activityResultLauncher

            callback(btnSubmit, btnCancel, vehicleDropdown, imgPreview, dialog)

        }
    }
}