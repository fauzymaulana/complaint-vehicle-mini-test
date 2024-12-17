package com.appero.vehiclecomplaint.domain.repositories

import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

interface VehicleRepository {
    fun getAllVehicle(): Single<Result<List<VehicleResponse>>>
}