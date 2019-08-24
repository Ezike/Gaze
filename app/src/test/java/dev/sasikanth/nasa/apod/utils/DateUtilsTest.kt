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
    fun `date1 is not after date2`() {
        val date1 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 30)
        }.time

        val date2 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 10)
        }.time

        // Even if both the dates have different times, the extension function will only check
        // (dd, MM, yyyy).
        assertFalse(date1.isAfter(date2))
    }

    /**
     * Testing if date1 is after date2, it will pass if date1 is after date2
     */
    @Test
    fun `date1 is after date2`() {
        val date1 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 23)
        }.time

        val date2 = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 22)
        }.time

        assertTrue(date1.isAfter(date2))
    }
}
