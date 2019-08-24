package dev.sasikanth.nasa.apod.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.databinding.PictureDetailItemBinding

private val VIEWER_DIFF = object : DiffUtil.ItemCallback<APod>() {
    override fun areItemsTheSame(oldItem: APod, newItem: APod): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: APod, newItem: APod): Boolean {
        return oldItem == newItem
    }
}

class ViewerAdapter : PagedListAdapter<APod, ViewerAdapter.ViewerItemHolder>(VIEWER_DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerItemHolder {
        return ViewerItemHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewerItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewerItemHolder private constructor(
        private val binding: PictureDetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewerItemHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PictureDetailItemBinding.inflate(layoutInflater, parent, false)
                return ViewerItemHolder(binding)
            }
        }

        fun bind(aPod: APod?) {
            binding.aPod = aPod
            binding.executePendingBindings()
        }

        fun imageViewScaleType(scaleType: ImageView.ScaleType) {
            binding.apodImage.scaleType = scaleType
        }
    }
}
