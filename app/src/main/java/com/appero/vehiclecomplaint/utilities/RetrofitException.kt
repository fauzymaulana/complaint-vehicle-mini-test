package com.klik.adzkia.core.utilities

import java.io.IOException
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitException(
    _message: String?,
    val code: Int?,
    private val _response: Response<*>?,
    val kind: Kind,
    _exception: Throwable?,
    private val _retrofit: Retrofit?,
) : RuntimeException(_message, _exception) {

    companion object {
        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, 1, null, Kind.NETWORK, exception, null)
        }

        fun jsonFormatError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, 2, null, Kind.UNEXPECTED, exception, null)
        }

    }

    enum class Kind {
        NETWORK,
        UNEXPECTED
    }
}