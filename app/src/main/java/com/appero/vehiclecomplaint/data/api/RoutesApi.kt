package com.appero.vehiclecomplaint.data.api

class RoutesApi {
    companion object {
        private const val PLATFORM = "/api/android"
        const val ALL_COMPLAINT_REPORT = "$PLATFORM/read_all_laporan"
        const val ALL_VEHICLE = "$PLATFORM/list_vehicle"
        const val ADD_VEHICLE = "$PLATFORM/add_laporan"
    }
}