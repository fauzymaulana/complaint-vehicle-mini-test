package com.appero.vehiclecomplaint.data.entities.remote

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    @field:SerializedName("reportId")
    val reportId: String?,

    @field:SerializedName("vehicleId")
    val vehicleId: String?,

    @field:SerializedName("vehicleLicenseNumber")
    val vehicleLicenseNumber: String?,

    @field:SerializedName("vehicleName")
    val vehicleName: String?,

    @field:SerializedName("note")
    val note: String?,

    @field:SerializedName("photo")
    val photo: String?,

    @field:SerializedName("createdAt")
    val createdAt: String?,

    @field:SerializedName("createdBy")
    val createdBy: String?,

    @field:SerializedName("reportStatus")
    val reportStatus: String?
)