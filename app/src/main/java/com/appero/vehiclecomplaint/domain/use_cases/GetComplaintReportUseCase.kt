package com.appero.vehiclecomplaint.domain.use_cases

import com.appero.vehiclecomplaint.data.entities.remote.ReportResponse
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.domain.mapper.toListEntity
import com.appero.vehiclecomplaint.domain.repositories.ComplaintReportRepository
import com.appero.vehiclecomplaint.utilities.ResultState
import com.appero.vehiclecomplaint.utilities.responseBaseDataSourceApiToResultState
import com.appero.vehiclecomplaint.utilities.responseErrorToResultStateError
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

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