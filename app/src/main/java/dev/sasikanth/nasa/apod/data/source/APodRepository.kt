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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

class APodRepository
@Inject constructor(
    private val localSource: APodDao,
    private val remoteSource: APodApiService
) {

    private val aPodBoundaryCallback = APodBoundaryCallback(
        localSource = localSource,
        remoteSource = remoteSource
    )
    val networkState = aPodBoundaryCallback.networkState

    suspend fun getLatestAPod() {
        // I could probably move this in onItemAtFrontLoaded of BoundaryCallback.
        // But I wanted to avoid checking every time top the page is reached.
        // This method is only called and checked every time MainViewModel is created,
        // so essentially when app is first opened.
        val currentCal = Calendar.getInstance(DateUtils.americanTimeZone)
        val lastLatestAPod = localSource.getLatestAPodDate()

        if (lastLatestAPod != null) {
            val lastLatestAPodCal = Calendar.getInstance().apply {
                time = lastLatestAPod
            }

            // Request latest picture only if current date is greater than
            // last saved date.
            if (currentCal.isAfter(lastLatestAPodCal)) {
                // Load latest APod from API
                val currentDate = DateUtils.formatDate(currentCal.time)
                try {
                    val aPodResponse = withContext(Dispatchers.IO) {
                        remoteSource.getAPod(BuildConfig.API_KEY, currentDate)
                    }
                    if (aPodResponse.isSuccessful) {
                        val latestApod = aPodResponse.body()
                        // Ignoring null since db is our source of truth, data won't change
                        if (latestApod != null) {
                            localSource.insertAPod(latestApod)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e.localizedMessage)
                }
            }
        }
    }

    fun getAPods(): LiveData<PagedList<APod>> {
        // PagedList config
        // While it's recommended to have prefetch distance as large as possible compared
        // to page size. In order to avoid calling API calls even before reaching end, I have
        // set prefetch distance to 10
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setPrefetchDistance(10)
            .build()

        // Building LivePagedList
        return LivePagedListBuilder(localSource.getAPods(), pagedListConfig)
            .setBoundaryCallback(aPodBoundaryCallback)
            .build()
    }
}
