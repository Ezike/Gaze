package dev.sasikanth.nasa.apod.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Observable object for single use only
 *
 * Live Data object that can attach only one observer to itself
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Timber.w("Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner, Observer<T> {
            if (pending.compareAndSet(true, false)) observer.onChanged(it)
        })
    }

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    /**
     * Resets object value and triggers observer
     */
    @MainThread
    fun call() {
        value = null
    }

}