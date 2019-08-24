package dev.sasikanth.nasa.apod

import android.app.Application
import dev.sasikanth.nasa.apod.di.app.AppComponent
import dev.sasikanth.nasa.apod.di.app.DaggerAppComponent
import dev.sasikanth.nasa.apod.di.misc.ComponentProvider
import timber.log.Timber

class NasaAPod : Application(), ComponentProvider {

    override val component: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
