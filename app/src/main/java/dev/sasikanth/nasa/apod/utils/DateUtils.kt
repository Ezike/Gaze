package dev.sasikanth.nasa.apod.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    val americanTimeZone: TimeZone = TimeZone.getTimeZone("America/Los_Angeles")
    private val americanDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = americanTimeZone
    }

    fun formatDate(date: Date): String = americanDateFormat.format(date)

    fun parseDate(date: String): Date = americanDateFormat.parse(date)!!
}

fun Calendar.isAfter(`when`: Calendar): Boolean {
    return this.get(Calendar.DAY_OF_MONTH) > `when`.get(Calendar.DAY_OF_MONTH) &&
            this.get(Calendar.MONTH) >= `when`.get(Calendar.MONTH) &&
            this.get(Calendar.YEAR) >= `when`.get(Calendar.YEAR)
}
