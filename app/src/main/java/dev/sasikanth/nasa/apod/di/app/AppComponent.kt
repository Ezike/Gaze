package dev.sasikanth.nasa.apod.di.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.nasa.apod.ui.MainActivity
import dev.sasikanth.nasa.apod.ui.MainViewModel
import dev.sasikanth.nasa.apod.ui.pages.grid.PicturesGridFragment
import dev.sasikanth.nasa.apod.ui.pages.viewer.ViewerFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    val mainViewModel: MainViewModel

    fun inject(mainActivity: MainActivity)
    fun inject(picturesGridFragment: PicturesGridFragment)
    fun inject(viewerFragment: ViewerFragment)
}