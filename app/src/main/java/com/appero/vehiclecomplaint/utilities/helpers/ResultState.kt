package com.appero.vehiclecomplaint.utilities.helpers

sealed class ResultState<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null,
) {
    class Success<T>(data: T) : ResultState<T>(data)

    class Loading<T> : ResultState<T>()

    class HideLoading<T> : ResultState<T>()

    class BadRequest<T>(message: String, data: T?, code: Int) : ResultState<T>(data, message, code)

    class Unauthorized<T>(message: String, data: T?, code: Int) :
        ResultState<T>(data, message, code)

    class NoConnection<T>(message: String, data: T?, code: Int) :
        ResultState<T>(data, message, code)

    class NotFound<T>(message: String, data: T?, code: Int) : ResultState<T>(data, message, code)

    class Forbidden<T>(message: String, data: T?, code: Int) : ResultState<T>(data, message, code)

    class Conflict<T>(message: String, data: T?, code: Int) : ResultState<T>(data, message, code)

    class UnknownError<T>(message: String, data: T?, code: Int) :
        ResultState<T>(data, message, code)

    class Timeout<T>(message: String, data: T?, code: Int) :
        ResultState<T>(data, message, code)

}
