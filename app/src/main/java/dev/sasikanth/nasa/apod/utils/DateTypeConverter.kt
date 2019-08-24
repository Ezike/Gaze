package dev.sasikanth.nasa.apod.utils

import androidx.room.TypeConverter
import java.util.Date

object DateTypeConverter {

    @JvmStatic
    @TypeConverter
    fun toDate(value: String): Date {
        return DateUtils.parseDate(value)
    }

    @JvmStatic
    @TypeConverter
    fun fromDate(date: Date): String {
        return DateUtils.formatDate(date)
    }
}
