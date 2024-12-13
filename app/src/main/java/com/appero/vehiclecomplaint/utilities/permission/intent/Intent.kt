package com.appero.vehiclecomplaint.utilities.permission.intent

import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun openPermissionSetting() : Intent {
    return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", "com.appero.vehiclecomplaint", null)
    }
}