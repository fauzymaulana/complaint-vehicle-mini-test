package com.appero.vehiclecomplaint.domain.mapper

import com.appero.vehiclecomplaint.data.entities.remote.FormVehicleRequest
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import com.appero.vehiclecomplaint.domain.entities.FormVehicle
import com.appero.vehiclecomplaint.domain.entities.Vehicle

fun VehicleResponse?.toVehicleEntity(): Vehicle {
    return Vehicle(
        vehicleId = this?.vehicleId.toString(),
        licenseNumber = this?.licenseNumber.toString(),
        type = this?.type.toString()
    )
}

fun FormVehicle.toFormVehicleReq(): FormVehicleRequest {
    return FormVehicleRequest(
        vehicleId = this.vehicleId,
        userId = this.userId,
        photo = this.photo,
        notes = this.notes
    )
}