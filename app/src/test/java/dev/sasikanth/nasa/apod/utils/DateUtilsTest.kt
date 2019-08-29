package dev.sasikanth.nasa.apod.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    /**
     * Testing if date1 is not after date2, it will pass if date1 is not after date2.
     */
    @Test
    fun `cal1 is not after cal2`() {
        val cal1 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 30)
        }

        val cal2 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 10)
        }

        // Even if both the dates have different times, the extension function will only check
        // (dd, MM, yyyy).
        assertFalse(cal1.isAfter(cal2))
    }

    /**
     * Testing if date1 is after date2, it will pass if date1 is after date2
     */
    @Test
    fun `cal1 is after cal2`() {
        val cal1 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 23)
        }

        val cal2 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
        }

        assertTrue(cal1.isAfter(cal2))
    }

    @Test
    fun `cal1 is after cal2 with different timezone`() {
        val cal1 = Calendar.getInstance(DateUtils.americanTimeZone)
        val cal2 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 23)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.YEAR, 2019)
        }

        assertTrue(cal1.isAfter(cal2))
    }

    /**
     * Check if the given date is formatted as yyyy-MM-dd
     */
    @Test
    fun `check if date is formatted to given format`() {
        val cal = Calendar.getInstance()
        val formattedDate = DateUtils.formatDate(cal.time)
        // Doing a general regex check for yyyy-MM-dd format
        val regexPattern = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"

        assertTrue(formattedDate.matches(Regex(regexPattern)))
    }

    /**
     * Check if the given date is formatted as MM/dd/yyyy
     */
    @Test
    fun `check if date is formatted to app format`() {
        val cal = Calendar.getInstance()
        val formattedDate = DateUtils.formatToAppDate(cal.time)
        val regexPattern = "^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/\\d{4}$"

        assertTrue(formattedDate.matches(Regex(regexPattern)))
    }

    /**
     * Parse yyyy-MM-dd formatted string into date object
     */
    @Test
    fun `parse correct formatted string`() {
        val formattedDate = "2019-02-14"
        val date = try {
            DateUtils.parseDate(formattedDate)
        } catch (e: Exception) {
            null
        }
        assertNotNull(date)
    }

    /**
     * Parse wrongly formatted string
     */
    @Test
    fun `parse wrongly formatted string`() {
        val formattedDate = "02/14/1994"
        val date = try {
            DateUtils.parseDate(formattedDate)
        } catch (e: Exception) {
            null
        }
        assertNull(date)
    }
}
