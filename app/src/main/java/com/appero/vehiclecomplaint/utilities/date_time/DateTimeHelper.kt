package com.appero.vehiclecomplaint.utilities.date_time

import com.appero.vehiclecomplaint.utilities.Constant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateTimeHelper {
    fun parseDateTime(dateTime: String): LocalDateTime {
        val inputFormatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT_DEFAULT, Locale(Constant.LANG, Constant.COUNTRY))
        return LocalDateTime.parse(dateTime, inputFormatter)
    }

    fun formatDateTime(parsedDateTime: LocalDateTime): String {
        val outputFormatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT_CUSTOM, Locale(Constant.LANG, Constant.COUNTRY))
        return parsedDateTime.format(outputFormatter)
    }
}