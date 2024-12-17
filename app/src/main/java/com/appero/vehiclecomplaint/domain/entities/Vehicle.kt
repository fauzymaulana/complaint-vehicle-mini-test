package com.appero.vehiclecomplaint.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vehicle(
    val vehicleId: String,
    val licenseNumber: String,
    val type: String
): Parcelable