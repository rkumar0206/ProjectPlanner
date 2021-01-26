package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants
import com.rohitthebest.projectplanner.databinding.AdapterTopicLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.utils.hideViewBySlidingAnimation
import com.rohitthebest.projectplanner.utils.showViewBySlidingAnimation
import com.rohitthebest.projectplanner.utils.strikeThrough

private const val TAG = "TopicAdapter"

class TopicAdapter : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class TopicViewHolder(val binding: AdapterTopicLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(topic: Topic?) {

            topic?.let {

                binding.tvTopicName.text = it.topicName
                binding.checkBoxTopicName.isChecked = it.isCompleted == Constants.TRUE

                //checking if the topic is completed
                if (it.topicName.isNotEmpty() && it.isCompleted == Constants.TRUE) {

                    binding.checkBoxTopicName.isChecked = true

                    binding.tvTopicName.strikeThrough(it.topicName)
                }
            }
        }


        init {

            binding.tvTopicName.setOnClickListener {

                if (binding.chooseOptionLL.visibility != View.VISIBLE) {

                    binding.chooseOptionLL.showViewBySlidingAnimation()
                    binding.clearTopicButton.showViewBySlidingAnimation()
                } else {

                    binding.chooseOptionLL.hideViewBySlidingAnimation(View.GONE)
                    binding.clearTopicButton.hideViewBySlidingAnimation(View.VISIBLE)
                }
            }

            binding.clearTopicButton.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onClearTopicButtonClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                }
            }

            binding.checkBoxTopicName.setOnCheckedChangeListener { _, isChecked ->

                try {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onTopicCheckChanged(getItem(absoluteAdapterPosition), absoluteAdapterPosition, isChecked)
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

    class DiffUtilCallback : DiffUtil.ItemCallback<Topic>() {

        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem.topicKey == newItem.topicKey

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {

        val binding = AdapterTopicLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        //topic functions
        fun onItemClick(topic: Topic)
        fun onClearTopicButtonClicked(topic: Topic, position: Int)
        fun onTopicCheckChanged(topic: Topic, position: Int, isChecked: Boolean)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
