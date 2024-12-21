package com.appero.vehiclecomplaint.data.data_sources

import com.appero.vehiclecomplaint.data.api.ComplaintService
import com.appero.vehiclecomplaint.data.entities.remote.FormVehicleRequest
import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.adapter.rxjava2.Result

interface ComplaintRemoteDataSource {
    fun getAllComplaint(userId: String): Single<Result<List<ReportResponse>>>
    fun getAllVehicle(): Single<Result<List<VehicleResponse>>>
    fun postAddComplaint(
        reqBody: Map<String, @JvmSuppressWildcards RequestBody>,
        imageBody: MultipartBody.Part
    ): Single<Result<Response>>
}

class ComplaintRemoteDataSourceImpl(
    private val api: ComplaintService
): ComplaintRemoteDataSource {

    override fun getAllComplaint(userId: String): Single<Result<List<ReportResponse>>> {
        return api.getAllComplaint(userId)
    }

    override fun getAllVehicle(): Single<Result<List<VehicleResponse>>> {
        return api.getAllVehicle()
    }

    override fun postAddComplaint(
        reqBody: Map<String, RequestBody>,
        imageBody: MultipartBody.Part
    ): Single<Result<Response>> {
        return api.postComplaint(reqBody, imageBody)
    }
}