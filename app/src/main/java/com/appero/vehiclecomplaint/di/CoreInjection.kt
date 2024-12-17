package com.appero.vehiclecomplaint.di

import com.appero.vehiclecomplaint.data.api.ComplaintConfig
import com.appero.vehiclecomplaint.data.data_sources.ComplaintRemoteDataSourceImpl
import com.appero.vehiclecomplaint.data.repositories.ComplaintReportRepositoryImpl
import com.appero.vehiclecomplaint.data.repositories.VehicleRepositoryImpl
import com.appero.vehiclecomplaint.domain.use_cases.GetComplaintReportUseCaseImpl
import com.appero.vehiclecomplaint.domain.use_cases.GetVehicleUseCaseImpl

class CoreInjection {
    private val configApi by lazy { ComplaintConfig.complaintApi() }
    private val remoteDataSource by lazy { ComplaintRemoteDataSourceImpl(configApi) }
    private val reportRepository by lazy { ComplaintReportRepositoryImpl(remoteDataSource) }
    private val vehicleRepository by lazy { VehicleRepositoryImpl(remoteDataSource) }

    val getAllComplaintUseCase by lazy { GetComplaintReportUseCaseImpl(reportRepository) }
    val getAllVehicleUseCase by lazy { GetVehicleUseCaseImpl(vehicleRepository) }
}