package com.appero.vehiclecomplaint.data.repositories

import com.appero.vehiclecomplaint.data.data_sources.ComplaintRemoteDataSource
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import com.appero.vehiclecomplaint.domain.repositories.VehicleRepository
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

class VehicleRepositoryImpl(
    private val remoteDataSource: ComplaintRemoteDataSource
): VehicleRepository {
    override fun getAllVehicle(): Single<Result<List<VehicleResponse>>> {
        return remoteDataSource.getAllVehicle()
    }
}