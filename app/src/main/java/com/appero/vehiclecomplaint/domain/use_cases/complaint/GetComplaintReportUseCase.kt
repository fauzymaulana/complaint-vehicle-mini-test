package com.appero.vehiclecomplaint.domain.use_cases.complaint

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.domain.mapper.toListEntity
import com.appero.vehiclecomplaint.domain.repositories.ComplaintReportRepository
import com.appero.vehiclecomplaint.utilities.helpers.ResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseBaseDataSourceApiToResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseErrorToResultStateError
import io.reactivex.Single

interface GetComplaintReportUseCase {
    operator fun invoke(userId: String): Single<ResultState<List<Report>>>
}

class GetComplaintReportUseCaseImpl(
    private val repo: ComplaintReportRepository
): GetComplaintReportUseCase {

    override fun invoke(userId: String): Single<ResultState<List<Report>>> {
        return repo.getAllReport(userId)
            .flatMap { response ->
                val z = responseBaseDataSourceApiToResultState(response.response()) {
                    it.toListEntity<ReportResponse, Report>()
                }
//                val statusCode = response.response()
//                val a = response.response()?.body()
//                val c = if (response.response()!!.isSuccessful) {
//                    response.response()!!.body()
//                } else {
//                    response.error()?.message
//                }

                Single.just(z)
            }
            .onErrorReturn { err ->
                return@onErrorReturn responseErrorToResultStateError(err)
            }
    }
}