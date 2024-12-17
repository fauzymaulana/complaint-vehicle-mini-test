package com.appero.vehiclecomplaint.domain.mapper

import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import com.appero.vehiclecomplaint.domain.entities.Vehicle

fun VehicleResponse?.toVehicleEntity(): Vehicle {
    return Vehicle(
        vehicleId = this?.vehicleId.toString(),
        licenseNumber = this?.licenseNumber.toString(),
        type = this?.type.toString()
    )
}