package dev.sasikanth.nasa.apod.ui.pages.viewer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import dev.sasikanth.nasa.apod.BR.mainViewModel
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.databinding.FragmentViewerBinding
import dev.sasikanth.nasa.apod.di.misc.activityViewModels
import dev.sasikanth.nasa.apod.di.misc.injector
import dev.sasikanth.nasa.apod.services.PictureDownloadService
import dev.sasikanth.nasa.apod.ui.MainViewModel
import dev.sasikanth.nasa.apod.ui.adapters.ViewerAdapter
import dev.sasikanth.nasa.apod.utils.ZoomOutPageTransformer
import dev.sasikanth.nasa.apod.utils.extensions.observe


class ViewerFragment : Fragment() {

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
        private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private val viewModel: MainViewModel by activityViewModels {
        requireActivity().injector.mainViewModel
    }

    private lateinit var binding: FragmentViewerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewerBinding.inflate(inflater).apply {
            lifecycleOwner = this@ViewerFragment
            mainViewModel = viewModel
        }

        val viewerAdapter = ViewerAdapter()

        binding.apodsViewer.apply {
            adapter = viewerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setPageTransformer(ZoomOutPageTransformer())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setPosition(position)
                    val currentAPod = viewModel.getCurrentAPod()
                    if (currentAPod != null) {
                        binding.aPod = currentAPod
                        binding.executePendingBindings()
                    }
                }
            })
        }

        binding.exitPictureDetail.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.aPods.observe(viewLifecycleOwner, Observer {
            viewerAdapter.submitList(it)
            binding.apodsViewer.setCurrentItem(viewModel.getPosition(), false)
        })

        binding.apodsViewer.transitionName = "${viewModel.getPosition()}"

        observe(viewModel.downloadEvent) { downloadImage(binding.aPod) }
        observe(viewModel.showInfoEvent) { showPictureInformation(binding.aPod) }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, download image
                    viewModel.downloadEvent.call()
                } else {
                    // Permission not granted
                    Toast.makeText(
                        requireContext(),
                        "Storage permission is required to download",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPictureInformation(aPod: APod?) {
        aPod?.let {
            findNavController().navigate(ViewerFragmentDirections.actionPictureInformation(it))
        }

    }

    private fun downloadImage(aPod: APod?) {
        aPod?.let {
            if (allPermissionsGranted()) {
                // Storage permission is granted, trigger download service
                PictureDownloadService.startService(requireContext(), it.title, it.hdUrl)
            } else {
                // Storage permission is not given, show dialog and ask for permission
                requestPermissions(PERMISSIONS, STORAGE_PERMISSION_REQUEST_CODE)
            }
        }

    }

    private fun allPermissionsGranted(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
