package com.appero.vehiclecomplaint.data.repositories

import com.appero.vehiclecomplaint.data.data_sources.ComplaintRemoteDataSource
import com.appero.vehiclecomplaint.data.entities.remote.FormVehicleRequest
import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.domain.repositories.ComplaintReportRepository
import com.appero.vehiclecomplaint.data.config.networking.createImageRequestBody
import com.appero.vehiclecomplaint.data.config.networking.createRequestBody
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.adapter.rxjava2.Result
import java.io.File

class ComplaintReportRepositoryImpl(
    private val remoteDataSource: ComplaintRemoteDataSource
): ComplaintReportRepository {
    override fun getAllReport(userId: String): Single<Result<List<ReportResponse>>> {
        return remoteDataSource.getAllComplaint(userId)
    }

    override fun addComplaint(formReq: FormVehicleRequest): Single<Result<Response>> {
        val params = mapOf(
            "vehicleId" to formReq.vehicleId.createRequestBody(),
            "userId" to formReq.userId.createRequestBody(),
            "note" to formReq.notes.createRequestBody()
        )

        val file = File(formReq.photo)
        val imagePart = MultipartBody.Part.createFormData("photo", file.name, file.createImageRequestBody())

        return remoteDataSource.postAddComplaint(
            reqBody = params,
            imageBody = imagePart
        )
    }
}