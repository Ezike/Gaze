package dev.sasikanth.nasa.apod.ui.pages.grid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.sasikanth.nasa.apod.databinding.FragmentPicturesGridBinding

class PicturesGridFragment : Fragment() {

    private lateinit var binding: FragmentPicturesGridBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPicturesGridBinding.inflate(inflater)
        return binding.root
    }
}