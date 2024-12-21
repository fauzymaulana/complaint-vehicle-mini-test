package com.appero.vehiclecomplaint.data.config.networking

import com.appero.vehiclecomplaint.utilities.Constant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun String.createRequestBody(): RequestBody {
    return this.toRequestBody(Constant.HEADER_TEXT_PLAIN.toMediaTypeOrNull())
}

fun File.createImageRequestBody(): RequestBody {
    return this.asRequestBody(Constant.HEADER_IMAGE.toMediaTypeOrNull())
}