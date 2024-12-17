package com.appero.vehiclecomplaint.domain.mapper

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse

fun <T,R> List<T>?.toListEntity(): List<R> {
    val itemList = mutableListOf<R>()
    if (!this.isNullOrEmpty()) {
        this.forEach {
            when(it) {
                is ReportResponse -> {
                    itemList.add(it.toReportEntity() as R)
                }

                is VehicleResponse -> {
                    itemList.add(it.toVehicleEntity() as R)
                }
            }
        }
    }

    return itemList
}