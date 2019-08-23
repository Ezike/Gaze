package dev.sasikanth.nasa.apod.data.source

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.data.source.local.APodDao
import dev.sasikanth.nasa.apod.data.source.local.APodDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class APodDaoTest {

    private lateinit var database: APodDatabase
    private lateinit var aPodDao: APodDao

    @Before
    fun init() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            appContext,
            APodDatabase::class.java
        ).build()
        aPodDao = database.aPodDao
    }

    @Test
    fun insertAndRetrieveAPod() {
        val aPod = APod(
            date = "2019-08-22",
            title = "Nearby Spiral Galaxy NGC 4945",
            explanation = "Large spiral galaxy NGC 4945 is seen edge-on near the center of this cosmic galaxy portrait. In fact, it's almost the size of our Milky Way Galaxy. NGC 4945's own dusty disk, young blue star clusters, and pink star forming regions standout in the sharp, colorful telescopic image. About 13 million light-years distant toward the expansive southern constellation Centaurus, NGC 4945 is only about six times farther away than Andromeda, the nearest large spiral galaxy to the Milky Way. Though this galaxy's central region is largely hidden from view for optical telescopes, X-ray and infrared observations indicate significant high energy emission and star formation in the core of NGC 4945. Its obscured but active nucleus qualifies the gorgeous island universe as a Seyfert galaxy and home to a central supermassive black hole.",
            copyright = "Martin Pugh",
            mediaType = "image",
            serviceVersion = "v1",
            thumbnailUrl = "https://apod.nasa.gov/apod/image/1908/NGC4945_HaLRGB.jpg",
            hdUrl = "https://apod.nasa.gov/apod/image/1908/NGC4945_HaLRGB.jpg"
        )

        runBlocking {
            // Insert APod
            aPodDao.insertAPod(aPod)
            // Retrieve APod
            aPodDao.getAPod(aPod.date).also {
                if (it != null) {
                    assertEquals(aPod.title, it.title)
                }
            }
        }
    }

    @After
    fun destroy() {
        database.close()
    }
}
