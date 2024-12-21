package com.appero.vehiclecomplaint.data.entities.remote


data class FormVehicleRequest(
    var vehicleId: String,
    var userId: String,
    var photo: String,
    var notes: String
)