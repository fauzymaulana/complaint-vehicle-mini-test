package com.appero.vehiclecomplaint.di

import com.appero.vehiclecomplaint.data.api.ComplaintConfig
import com.appero.vehiclecomplaint.data.data_sources.ComplaintRemoteDataSourceImpl
import com.appero.vehiclecomplaint.data.repositories.ComplaintReportRepositoryImpl
import com.appero.vehiclecomplaint.domain.use_cases.GetComplaintReportUseCaseImpl

class CoreInjection {
    private val configApi by lazy { ComplaintConfig.complaintApi() }
    private val remoteDataSource by lazy { ComplaintRemoteDataSourceImpl(configApi) }
    private val reportRepository by lazy { ComplaintReportRepositoryImpl(remoteDataSource) }

    val getAllComplaintUseCase by lazy { GetComplaintReportUseCaseImpl(reportRepository) }
}