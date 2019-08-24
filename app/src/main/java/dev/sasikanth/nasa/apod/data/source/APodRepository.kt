package dev.sasikanth.nasa.apod.data.source

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.sasikanth.nasa.apod.BuildConfig
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.data.source.local.APodDao
import dev.sasikanth.nasa.apod.data.source.remote.APodApiService
import dev.sasikanth.nasa.apod.utils.DateUtils
import dev.sasikanth.nasa.apod.utils.isAfter
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class APodRepository
@Inject constructor(
    private val localService: APodDao,
    private val remoteService: APodApiService
) {

    private val aPodBoundaryCallback = APodBoundaryCallback(
        localSource = localService,
        remoteSource = remoteService
    )
    val networkState = aPodBoundaryCallback.networkState

    suspend fun getLatestAPod() {
        val currentCal = Calendar.getInstance(DateUtils.americanTimeZone)
        val lastLatestAPod = localService.getLatestAPodDate()
        if (lastLatestAPod != null) {
            if (currentCal.time.isAfter(lastLatestAPod)) {
                // Load latest APod from API
                val currentDate = DateUtils.formatDate(currentCal.time)
                try {
                    val latestApod = remoteService.getAPod(BuildConfig.API_KEY, currentDate)
                    localService.insertAPod(latestApod)
                } catch (e: Exception) {
                    Timber.e(e.localizedMessage)
                }
            } else {
                Timber.d("Latest picture is already loaded!")
            }
        }
    }

    fun getAPods(): LiveData<PagedList<APod>> {
        // PagedList config
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setPrefetchDistance(10)
            .setEnablePlaceholders(false)
            .build()

        // Building LivePagedList
        return LivePagedListBuilder(localService.getAPods(), pagedListConfig)
            .setBoundaryCallback(aPodBoundaryCallback)
            .build()
    }
}
