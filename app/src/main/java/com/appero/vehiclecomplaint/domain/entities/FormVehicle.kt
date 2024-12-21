package com.appero.vehiclecomplaint.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormVehicle(
    var vehicleId: String,
    var userId: String,
    var photo: String,
    var notes: String
): Parcelable
