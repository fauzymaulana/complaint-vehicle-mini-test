package com.appero.vehiclecomplaint.data.entities.remote

import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @field:SerializedName("vehicleId")
    val vehicleId: String,

    @field:SerializedName("licenseNumber")
    val licenseNumber: String,

    @field:SerializedName("type")
    val type: String
)