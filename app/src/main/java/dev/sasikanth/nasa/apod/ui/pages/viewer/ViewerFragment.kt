package dev.sasikanth.nasa.apod.ui.pages.viewer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.databinding.FragmentViewerBinding
import dev.sasikanth.nasa.apod.di.misc.activityViewModels
import dev.sasikanth.nasa.apod.di.misc.injector
import dev.sasikanth.nasa.apod.ui.MainActivity
import dev.sasikanth.nasa.apod.ui.MainViewModel
import dev.sasikanth.nasa.apod.ui.adapters.ViewerAdapter
import dev.sasikanth.nasa.apod.utils.ZoomOutPageTransformer

// This can be moved into viewmodel as an single event live data
interface PictureInformationListener {
    fun showPictureInformation(aPod: APod)
}

class ViewerFragment : Fragment(), PictureInformationListener {

    private val viewModel: MainViewModel by activityViewModels {
        requireActivity().injector.mainViewModel
    }

    private lateinit var binding: FragmentViewerBinding

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewerBinding.inflate(inflater)
        binding.pictureInformationListener = this
        binding.lifecycleOwner = this

        val viewerAdapter = ViewerAdapter()

        binding.apodsViewer.apply {
            adapter = viewerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setPageTransformer(ZoomOutPageTransformer())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    MainActivity.currentPosition = position
                    val currentAPod = viewModel.aPods.value?.get(MainActivity.currentPosition)
                    binding.aPod = currentAPod
                    binding.executePendingBindings()
                }
            })
        }

        binding.exitPictureDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.aPods.observe(viewLifecycleOwner, Observer {
            viewerAdapter.submitList(it)
            binding.apodsViewer.setCurrentItem(MainActivity.currentPosition, false)
        })

        return binding.root
    }

    override fun showPictureInformation(aPod: APod) {
        findNavController().navigate(ViewerFragmentDirections.actionPictureInformation(aPod))
    }
}
