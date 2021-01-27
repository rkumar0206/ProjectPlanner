package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterTechnologyLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Technology

class TechnologyAdapter :
    ListAdapter<Technology, TechnologyAdapter.TechnologyViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class TechnologyViewHolder(var binding: AdapterTechnologyLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(technology: Technology?) {

            technology?.let {

                binding.adapterTechNameTV.text = it.name
                it.backgroundColor?.let { it1 -> binding.root.setCardBackgroundColor(it1) }
                it.textColor?.let { it1 -> binding.adapterTechNameTV.setTextColor(it1) }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Technology>() {

        override fun areItemsTheSame(oldItem: Technology, newItem: Technology): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Technology, newItem: Technology): Boolean =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnologyViewHolder {

        val binding = AdapterTechnologyLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TechnologyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TechnologyViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(technology: Technology)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
