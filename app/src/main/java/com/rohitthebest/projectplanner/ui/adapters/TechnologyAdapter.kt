package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.SHOW_UI
import com.rohitthebest.projectplanner.databinding.AdapterTechnologyLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Technology
import com.rohitthebest.projectplanner.utils.invisible

class TechnologyAdapter(var showOrHideUi: String = SHOW_UI) :
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

        init {

            if (showOrHideUi != SHOW_UI) {

                binding.techAdapterDeleteBtn.invisible()
            } else {

                binding.root.setOnClickListener {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onTechnologyClicked(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )
                    }
                }

                binding.techAdapterDeleteBtn.setOnClickListener {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onTechnologyDeleteBtnClicked(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )
                    }
                }
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
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

        fun onTechnologyClicked(technology: Technology, position: Int)
        fun onTechnologyDeleteBtnClicked(technology: Technology, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
