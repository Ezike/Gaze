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
    private val localService: APodDao,
    private val remoteService: APodApiService
) {

    private val aPodBoundaryCallback = APodBoundaryCallback(
        localSource = localService,
        remoteSource = remoteService
    )
    val networkState = aPodBoundaryCallback.networkState

    suspend fun getLatestAPod() {
        // I could probably move this in onItemAtFrontLoaded of BoundaryCallback.
        // But I wanted to avoid checking every time top the page is reached.
        // This method is only called and checked every time MainViewModel is created,
        // so essentially when app is first opened.
        val currentCal = Calendar.getInstance(DateUtils.americanTimeZone)
        val lastLatestAPod = localService.getLatestAPodDate()

        if (lastLatestAPod != null) {
            val lastLatestAPodCal = Calendar.getInstance().apply {
                time = lastLatestAPod
            }
            if (currentCal.isAfter(lastLatestAPodCal)) {
                // Load latest APod from API
                val currentDate = DateUtils.formatDate(currentCal.time)
                try {
                    val latestApod = withContext(Dispatchers.IO) {
                        remoteService.getAPod(BuildConfig.API_KEY, currentDate)
                    }
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
        // While it's recommended to have prefetch distance as large as possible compared
        // to page size. In order to avoid calling API calls even before reaching end, I have
        // set prefetch distance to 10
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setPrefetchDistance(10)
            .build()

        // Building LivePagedList
        return LivePagedListBuilder(localService.getAPods(), pagedListConfig)
            .setBoundaryCallback(aPodBoundaryCallback)
            .build()
    }
}
