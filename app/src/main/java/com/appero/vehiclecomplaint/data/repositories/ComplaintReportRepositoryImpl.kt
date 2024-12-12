package com.appero.vehiclecomplaint.data.repositories

import com.appero.vehiclecomplaint.data.data_sources.ComplaintRemoteDataSource
import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.domain.repositories.ComplaintReportRepository
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

class ComplaintReportRepositoryImpl(
    private val remoteDataSource: ComplaintRemoteDataSource
): ComplaintReportRepository {
    override fun getAllReport(userId: String): Single<Result<List<ReportResponse>>> {
        return remoteDataSource.getAllComplaint(userId)
    }
}