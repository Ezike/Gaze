package dev.sasikanth.nasa.apod.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    private val americanDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("America/Los_Angeles")
    }

    fun formatDate(date: Date): String = americanDateFormat.format(date)

    fun parseDate(date: String): Date = americanDateFormat.parse(date)
}