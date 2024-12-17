package com.appero.vehiclecomplaint.data.api

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ComplaintService {

    @GET(RoutesApi.ALL_COMPLAINT_REPORT)
    fun getAllComplaint(@Query("userId") userId: String): Single<Result<List<ReportResponse>>>

    @GET(RoutesApi.ALL_VEHICLE)
    fun getAllVehicle(): Single<Result<List<VehicleResponse>>>


}