package com.appero.vehiclecomplaint.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FormReportDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FormReportDialog : DialogFragment() {

    private var _binding: FormReportDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FormReportDialogBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogOverlayTheme)
        dialog.setView(binding.root)
        dialog.setBackgroundInsetStart(16)
        dialog.setBackgroundInsetEnd(16)

        val title = arguments?.getString(ARG_TITLE) ?: "Default Title"
        val message = arguments?.getString(ARG_MESSAGE) ?: "Default Message"

        // Set data to views
//        binding.tvTitle.text = title
//        binding.tvMessage.text = message
//
//        // Handle button click
//        binding.btnOk.setOnClickListener {
//            dismiss() // Close the dialog
//        }

        return dialog.create()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    override fun onStart() {
        super.onStart()
        val marginInPx = (21 * requireContext().resources.displayMetrics.density).toInt()
        dialog?.window?.apply {
            setLayout(
                requireContext().resources.displayMetrics.widthPixels - 2 * marginInPx,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
    }

    companion object {
        private const val ARG_TITLE = "argTitle"
        private const val ARG_MESSAGE = "argMessage"

        fun newInstance(title: String, message: String) = FormReportDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
//                putArra
            }
        }
    }
}