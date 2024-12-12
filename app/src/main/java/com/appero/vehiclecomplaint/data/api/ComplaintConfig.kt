package com.appero.vehiclecomplaint.data.api

import com.appero.vehiclecomplaint.utilities.networking.RequestClient

class ComplaintConfig {
    companion object {
        private val reqClient by lazy { RequestClient() }

        fun complaintApi() : ComplaintService {
            return reqClient.getClient().create(ComplaintService::class.java)
        }
    }
}