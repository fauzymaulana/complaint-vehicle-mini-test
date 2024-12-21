package com.appero.vehiclecomplaint.domain.use_cases.complaint

import com.appero.vehiclecomplaint.domain.entities.FormVehicle
import com.appero.vehiclecomplaint.domain.mapper.toFormVehicleReq
import com.appero.vehiclecomplaint.domain.repositories.ComplaintReportRepository
import com.appero.vehiclecomplaint.utilities.helpers.ResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseBaseDataSourceApiToResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseErrorToResultStateError
import io.reactivex.Single

interface PostComplaintReportUseCase {
    operator fun invoke(formReq: FormVehicle): Single<ResultState<Int>>
}

class PostComplaintReportUseCaseImpl(
    private val repo: ComplaintReportRepository
): PostComplaintReportUseCase {
    override fun invoke(formReq: FormVehicle): Single<ResultState<Int>> {
        return repo.addComplaint(formReq.toFormVehicleReq())
            .map { response ->
                responseBaseDataSourceApiToResultState(response.response()) {
                    it.code
                }
            }
            .onErrorReturn { err ->
                return@onErrorReturn responseErrorToResultStateError(err)
            }
    }
}