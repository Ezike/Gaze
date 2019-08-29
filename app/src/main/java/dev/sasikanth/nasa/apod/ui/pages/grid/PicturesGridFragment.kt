package dev.sasikanth.nasa.apod.ui.pages.grid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.nasa.apod.databinding.FragmentPicturesGridBinding
import dev.sasikanth.nasa.apod.di.misc.activityViewModels
import dev.sasikanth.nasa.apod.di.misc.injector
import dev.sasikanth.nasa.apod.ui.MainActivity
import dev.sasikanth.nasa.apod.ui.MainViewModel
import dev.sasikanth.nasa.apod.ui.adapters.APodItemListener
import dev.sasikanth.nasa.apod.ui.adapters.APodsGridAdapter

class PicturesGridFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels {
        requireActivity().injector.mainViewModel
    }

    private lateinit var binding: FragmentPicturesGridBinding

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPicturesGridBinding.inflate(inflater)

        val adapter = APodsGridAdapter(APodItemListener { position ->
            // Navigate to picture view
            MainActivity.currentPosition = position
            findNavController().navigate(PicturesGridFragmentDirections.actionShowPicture())
        })
        binding.apodsGrid.apply {
            this.adapter = adapter

            val layoutManager = binding.apodsGrid.layoutManager as GridLayoutManager
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == APodsGridAdapter.LOADING_ITEM) {
                        2
                    } else {
                        1
                    }
                }
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                // Inorder to save the scroll position on orientation change
                // we are getting first visible item position from the GridLayoutManager
                // and setting it as current position
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                        MainActivity.currentPosition = firstVisiblePosition
                    }
                }
            })
        }

        mainViewModel.aPods.observe(viewLifecycleOwner, Observer { pagedList ->
            adapter.submitList(pagedList)
        })
        mainViewModel.networkState.observe(viewLifecycleOwner, Observer { loadingState ->
            adapter.setLoadingState(loadingState)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollToPosition()
    }

    private fun scrollToPosition() {
        binding.apodsGrid.doOnLayout {
            val layoutManager = binding.apodsGrid.layoutManager
            val viewAtPosition = layoutManager?.findViewByPosition(MainActivity.currentPosition)
            if (viewAtPosition == null || layoutManager
                    .isViewPartiallyVisible(viewAtPosition, false, true)
            ) {
                binding.apodsGrid.post {
                    layoutManager?.scrollToPosition(MainActivity.currentPosition)
                }
            }
        }
    }
}
