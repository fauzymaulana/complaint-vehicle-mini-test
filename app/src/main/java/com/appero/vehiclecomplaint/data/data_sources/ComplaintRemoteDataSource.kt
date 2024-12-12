package com.appero.vehiclecomplaint.data.data_sources

import com.appero.vehiclecomplaint.data.api.ComplaintService
import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

interface ComplaintRemoteDataSource {
    fun getAllComplaint(userId: String): Single<Result<List<ReportResponse>>>
}

class ComplaintRemoteDataSourceImpl(
    private val api: ComplaintService
): ComplaintRemoteDataSource {

    override fun getAllComplaint(userId: String): Single<Result<List<ReportResponse>>> {
        return api.getAllComplaint(userId)
    }
}