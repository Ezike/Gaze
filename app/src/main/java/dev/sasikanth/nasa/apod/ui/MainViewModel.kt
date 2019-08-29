package dev.sasikanth.nasa.apod.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.data.source.APodRepository
import dev.sasikanth.nasa.apod.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    aPodRepository: APodRepository
) : ViewModel() {

    val networkState = aPodRepository.networkState
    val aPods: LiveData<PagedList<APod>> = aPodRepository.getAPods()

    val downloadEvent = SingleLiveEvent<APod>()
    val showInfoEvent = SingleLiveEvent<APod>()

    private var currentPosition = 0
        set(value) {
            // Making sure we are not setting a negative value as current position
            if (value >= 0) {
                field = value
            }
        }

    init {
        viewModelScope.launch {
            aPodRepository.getLatestAPod()
        }
    }

    fun downLoadImage() {
        downloadEvent.call()
    }

    fun showImageInfo() {
        showInfoEvent.call()
    }

    fun setPosition(i: Int) {
        currentPosition = i
    }

    fun getPosition(): Int = currentPosition

    fun getCurrentAPod(): APod? = aPods.value?.get(getPosition())
}
