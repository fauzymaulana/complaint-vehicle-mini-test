package com.appero.vehiclecomplaint.utilities.pop_up

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FormReportDialogBinding
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.appero.vehiclecomplaint.utilities.extensions.hideKeyboard
import com.appero.vehiclecomplaint.utilities.extensions.showKeyboard
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GeneralDialog(private val context: Context) {
    val formDialog by lazy { FormReportDialogBinding.inflate(LayoutInflater.from(context)) }
    private var pickImageLauncher: ActivityResultLauncher<String>? = null

    private fun setFocusableEditText(view: View) {
        view.showKeyboard(context)
        view.isFocusable = true
        view.requestFocus()
    }

    fun setFormDialog(
        dateTime: String,
        listVehicle: List<Vehicle>,
        imagePath: String?,
        cancelable: Boolean = false,
        activityResultLauncher: ActivityResultLauncher<String>,
        callback: (MaterialButton, MaterialButton, MaterialAutoCompleteTextView, ImageView, TextInputLayout, TextInputEditText, AlertDialog) -> Unit
    ) {
        formDialog.apply {
            txtDateTime.text = dateTime
            if (imagePath != null && imagePath != "") {
                Log.e("TAG", "setFormDialog: INI MAnA LGI IMAGENYA WOU ${imagePath}", )
                Glide.with(context)
                    .load(imagePath)
                    .override(56, 56)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.img_not_found)
                    .into(imgPreview)
            } else {
                Log.e("TAG", "setFormDialog: INI MAnA KOSONG", )
                imgPreview.setImageResource(R.drawable.ic_add_rounded)
            }

            vehicleDropdown.apply {
                setOnTouchListener { v, _ ->
                    v.performClick()
                    v.hideKeyboard(context)
                    false
                }

                setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.hideKeyboard(context) }
            }

            labelNotes.setOnClickListener {
                setFocusableEditText(etNotes)
            }
            containerNotes.setOnClickListener {
                setFocusableEditText(etNotes)
            }

            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, listVehicle.map { it.type })
            vehicleDropdown.setAdapter(adapter)

            val dialog = MaterialAlertDialogBuilder(context, R.style.DialogOverlayTheme)
                .setView(root)
                .setBackgroundInsetStart(16)
                .setBackgroundInsetEnd(16)
                .setCancelable(cancelable)
                .show()

            val marginInPx = (21 * context.resources.displayMetrics.density).toInt()
            dialog?.window?.apply {
                setBackgroundDrawableResource(R.drawable.background_bottom_rounded_10_radius)
                setLayout(
                    context.resources.displayMetrics.widthPixels - 2 * marginInPx,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }

            pickImageLauncher = activityResultLauncher

            callback(btnSubmit, btnCancel, vehicleDropdown, imgPreview, etNotesLayout, etNotes, dialog)
        }
    }
}