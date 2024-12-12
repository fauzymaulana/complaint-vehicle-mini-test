package com.appero.vehiclecomplaint.domain.mapper

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.domain.entities.Report

fun ReportResponse?.toReportEntity(): Report {
    return Report(
        reportId = this?.reportId,
        vehicleId = this?.vehicleId,
        vehicleLicenseNumber = this?.vehicleLicenseNumber,
        vehicleName = this?.vehicleName,
        note = this?.note,
        photo = this?.photo,
        createdAt = this?.createdAt,
        createdBy = this?.createdBy,
        reportStatus = this?.reportStatus
    )
}