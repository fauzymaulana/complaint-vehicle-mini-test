package com.appero.vehiclecomplaint.domain.repositories

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

interface ComplaintReportRepository {
    fun getAllReport(userId: String): Single<Result<List<ReportResponse>>>
}