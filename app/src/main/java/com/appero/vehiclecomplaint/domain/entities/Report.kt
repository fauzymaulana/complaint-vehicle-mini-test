package com.appero.vehiclecomplaint.domain.entities

data class Report(
    val reportId: String?,
    val vehicleId: String?,
    val vehicleLicenseNumber: String?,
    val vehicleName: String?,
    val note: String?,
    val photo: String?,
    val createdAt: String?,
    val createdBy: String?,
    val reportStatus: String?
)
