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

fun Date.isAfter(date: Date): Boolean {
    val cal1 = Calendar.getInstance().apply {
        time = this@isAfter
    }
    val cal2 = Calendar.getInstance().apply {
        time = date
    }
    return cal1.get(Calendar.DAY_OF_MONTH) > cal2.get(Calendar.DAY_OF_MONTH) &&
            cal1.get(Calendar.MONTH) >= cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR)
}
