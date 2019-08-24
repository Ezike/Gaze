package dev.sasikanth.nasa.apod.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.data.source.APodRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    aPodRepository: APodRepository
) : ViewModel() {

    val networkState = aPodRepository.networkState
    val aPods: LiveData<PagedList<APod>> = aPodRepository.getAPods()

    init {
        viewModelScope.launch {
            aPodRepository.getLatestAPod()
        }
    }
}