package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants
import com.rohitthebest.projectplanner.databinding.AdapterSubTopicLayoutBinding
import com.rohitthebest.projectplanner.db.entity.SubTopic
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.strikeThrough

class SubTopicAdapter : ListAdapter<SubTopic, SubTopicAdapter.SubTopicViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class SubTopicViewHolder(val binding: AdapterSubTopicLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(subTopic: SubTopic?) {

            subTopic?.let {

                binding.etSubTopicName.setText(it.subTopicName)

                binding.checkBoxSubTopic.isChecked = it.isCompleted == Constants.TRUE

                if (it.subTopicName.trim().isNotEmpty() && it.isCompleted == Constants.TRUE) {

                    binding.checkBoxSubTopic.isChecked = true

                    binding.etSubTopicName.strikeThrough(it.subTopicName)
                }

            }

            if (absoluteAdapterPosition == itemCount - 1) {

                binding.addAnotherSubTopicBtn.show()
            } else {

                binding.addAnotherSubTopicBtn.hide()
            }
        }

        init {

            binding.addAnotherSubTopicBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onAddAnotherSubTopicClicked()
                }
            }

            binding.checkBoxSubTopic.setOnCheckedChangeListener { _, isChecked ->

                try {

                    if (binding.etSubTopicName.text.toString().trim().isNotEmpty()) {

                        if (checkForNullability(absoluteAdapterPosition)) {

                            mListener!!.onSubTopicCheckChanged(getItem(absoluteAdapterPosition), absoluteAdapterPosition, isChecked)
                        }
                    } else {

                        binding.checkBoxSubTopic.isChecked = false
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }

            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<SubTopic>() {

        override fun areItemsTheSame(oldItem: SubTopic, newItem: SubTopic): Boolean =
                oldItem.subTopicKey == newItem.subTopicKey

        override fun areContentsTheSame(oldItem: SubTopic, newItem: SubTopic): Boolean =
                oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTopicViewHolder {

        val binding = AdapterSubTopicLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SubTopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubTopicViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(subTopic: SubTopic)

        fun onAddAnotherSubTopicClicked()
        fun onSubTopicCheckChanged(subTopic: SubTopic?, position: Int, isChecked: Boolean)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
