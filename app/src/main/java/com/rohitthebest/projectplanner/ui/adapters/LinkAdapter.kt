package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterShowSavedLinkBinding
import com.rohitthebest.projectplanner.db.entity.Url

class LinkAdapter : ListAdapter<Url, LinkAdapter.LinkViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class LinkViewHolder(val binding: AdapterShowSavedLinkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(url: Url?) {

            url?.let {

                binding.textViewLinkName.text = it.urlName
                binding.textViewUrl.text = it.url
            }
        }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Url>() {

        override fun areItemsTheSame(oldItem: Url, newItem: Url): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Url, newItem: Url): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {

        val binding =
            AdapterShowSavedLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(model: Url)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
