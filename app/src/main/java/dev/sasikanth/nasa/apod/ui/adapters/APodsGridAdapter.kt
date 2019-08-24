package dev.sasikanth.nasa.apod.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.nasa.apod.R
import dev.sasikanth.nasa.apod.data.APod
import dev.sasikanth.nasa.apod.data.NetworkState
import dev.sasikanth.nasa.apod.data.Status
import dev.sasikanth.nasa.apod.databinding.PictureItemBinding

private val APOD_DIFF = object : DiffUtil.ItemCallback<APod>() {
    override fun areItemsTheSame(oldItem: APod, newItem: APod): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: APod, newItem: APod): Boolean {
        return oldItem == newItem
    }
}

class APodsGridAdapter(
    private val aPodItemListener: APodItemListener
) : PagedListAdapter<APod, RecyclerView.ViewHolder>(APOD_DIFF) {

    companion object {
        const val LOADING_ITEM = 0
        const val APOD_ITEM = 1
    }

    private var networkState: NetworkState? = null

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setLoadingState(networkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = networkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            LOADING_ITEM
        } else {
            APOD_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOADING_ITEM -> NetworkStateItemViewHolder.from(parent)
            APOD_ITEM -> APodItemViewHolder.from(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is APodItemViewHolder) {
            holder.bind(getItem(position), aPodItemListener)
        } else if (holder is NetworkStateItemViewHolder) {
            holder.bind(networkState)
        }
    }

    class APodItemViewHolder private constructor(
        private val binding: PictureItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): APodItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PictureItemBinding.inflate(layoutInflater, parent, false)
                return APodItemViewHolder(binding)
            }
        }

        fun bind(aPod: APod?, aPodItemListener: APodItemListener) {
            binding.aPod = aPod
            binding.position = adapterPosition
            binding.aPodItemListener = aPodItemListener
            binding.executePendingBindings()
        }
    }

    class NetworkStateItemViewHolder private constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun from(parent: ViewGroup): NetworkStateItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
                return NetworkStateItemViewHolder(view)
            }
        }

        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val errMessage: AppCompatTextView = itemView.findViewById(R.id.err_msg)

        fun bind(networkState: NetworkState?) {
            progressBar.isVisible = networkState?.status == Status.LOADING
            errMessage.isVisible = networkState?.msg != null
            errMessage.text = networkState?.msg
        }
    }
}

class APodItemListener(val onClick: (position: Int) -> Unit) {
    fun click(position: Int) {
        onClick(position)
    }
}
