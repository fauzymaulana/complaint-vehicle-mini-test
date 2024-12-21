package com.appero.vehiclecomplaint.utilities.base

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.domain.entities.SnackBar
import com.appero.vehiclecomplaint.domain.entities.TypeSnackBar
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment: Fragment() {

    fun showSnackBarWithAction(
        snack: SnackBar,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val color = when(snack.status) {
            TypeSnackBar.SUCCESS -> R.color.green
            TypeSnackBar.ERROR -> R.color.red_error_snack_bar
            TypeSnackBar.PROGRESS -> com.google.android.material.R.color.m3_ref_palette_neutral20
        }

        val textColor = when(snack.status) {
            TypeSnackBar.SUCCESS, TypeSnackBar.ERROR -> R.color.white
            TypeSnackBar.PROGRESS -> com.google.android.material.R.color.m3_ref_palette_neutral20
        }

        val snackMessage = snack.message
        val snackBar = Snackbar.make(requireView(), snackMessage, Snackbar.LENGTH_LONG)
            .setBackgroundTint(
                ResourcesCompat.getColor(
                    resources,
                    color,
                    null
                )
            )
            .setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), textColor)))
        if (actionMessage != null) {
            snackBar.setAction(actionMessage) {
                action(requireView())
            }.show()
        } else {
            snackBar.show()
        }
    }

}