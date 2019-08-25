package dev.sasikanth.nasa.apod.utils

import org.junit.Assert.assertFalse
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
}
