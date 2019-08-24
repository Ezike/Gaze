package dev.sasikanth.nasa.apod.di.misc

import android.app.Activity
import dev.sasikanth.nasa.apod.di.app.AppComponent

interface ComponentProvider {
    val component: AppComponent
}

val Activity.injector get() = (application as ComponentProvider).component
