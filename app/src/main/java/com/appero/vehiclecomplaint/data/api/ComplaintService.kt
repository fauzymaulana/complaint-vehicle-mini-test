package com.appero.vehiclecomplaint.data.api

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ComplaintService {

    @GET(RoutesApi.ALL_COMPLAINT_REPORT)
    fun getAllComplaint(@Query("userId") userId: String): Single<Result<List<ReportResponse>>>

    @GET(RoutesApi.ALL_VEHICLE)
    fun getAllVehicle(): Single<Result<List<VehicleResponse>>>

    @Multipart
    @POST(RoutesApi.ADD_VEHICLE)
    fun postComplaint(
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part
    ): Single<Result<Response>>

}