package com.appero.vehiclecomplaint.domain.use_cases.vehicle

import com.appero.vehiclecomplaint.data.entities.remote.VehicleResponse
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.appero.vehiclecomplaint.domain.mapper.toListEntity
import com.appero.vehiclecomplaint.domain.repositories.VehicleRepository
import com.appero.vehiclecomplaint.utilities.helpers.ResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseBaseDataSourceApiToResultState
import com.appero.vehiclecomplaint.utilities.helpers.responseErrorToResultStateError
import io.reactivex.Single

interface GetVehicleUseCase {
    operator fun invoke(): Single<ResultState<List<Vehicle>>>
}

class GetVehicleUseCaseImpl(
    private val repo: VehicleRepository
): GetVehicleUseCase {
    override fun invoke(): Single<ResultState<List<Vehicle>>> {
        return repo.getAllVehicle()
            .map { response ->
                return@map responseBaseDataSourceApiToResultState(response.response()) {
                    it.toListEntity<VehicleResponse, Vehicle>()
                }
            }
            .onErrorReturn { err ->
                return@onErrorReturn responseErrorToResultStateError(err)
            }
    }
}