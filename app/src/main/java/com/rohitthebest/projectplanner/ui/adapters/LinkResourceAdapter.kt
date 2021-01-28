package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterLinkResourceLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Url

class LinkResourceAdapter :
    ListAdapter<Url, LinkResourceAdapter.LinkViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class LinkViewHolder(val binding: AdapterLinkResourceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(link: Url?) {

            link?.let {

                binding.apply {

                    adapterLinkNameTV.text = it.urlName
                    adapetrlinkTV.text = it.url
                }
            }
        }

        init {

            binding.root.setOnClickListener {

                mListener!!.onLinkClick(getItem(absoluteAdapterPosition))
            }

            binding.adapterEditLink.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onEditLinkButtonClicked(
                        getItem(absoluteAdapterPosition),
                        absoluteAdapterPosition
                    )
                }
            }

            binding.adapterDeleteLinkBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onDeleteLinkClicked(
                        getItem(absoluteAdapterPosition),
                        absoluteAdapterPosition
                    )
                }
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Url>() {

        override fun areItemsTheSame(oldItem: Url, newItem: Url): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Url, newItem: Url): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {

        val binding = AdapterLinkResourceLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {


        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onLinkClick(link: Url)
        fun onEditLinkButtonClicked(link: Url, position: Int)
        fun onDeleteLinkClicked(link: Url, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
