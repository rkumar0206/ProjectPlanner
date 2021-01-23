package com.rohitthebest.projectplanner.ui.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants
import com.rohitthebest.projectplanner.databinding.AdapterTopicLayoutBinding
import com.rohitthebest.projectplanner.db.entity.SubTopic
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.strikeThrough
import kotlinx.coroutines.*

private const val TAG = "TopicAdapter"

class TopicAdapter : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    private var viewPool = RecyclerView.RecycledViewPool()

    private lateinit var context: Context

    lateinit var subTopicAdapter: SubTopicAdapter

    inner class TopicViewHolder(val binding: AdapterTopicLayoutBinding) : RecyclerView.ViewHolder(binding.root), SubTopicAdapter.OnClickListener {

        fun setData(topic: Topic?) {

            topic?.let {

                binding.etTopicName.setText(it.topicName)
                binding.checkBoxTopicName.isChecked = it.isCompleted == Constants.TRUE

                //checking if the topic is completed
                if (it.topicName.isNotEmpty() && it.isCompleted == Constants.TRUE) {

                    binding.checkBoxTopicName.isChecked = true

                    binding.etTopicName.strikeThrough(it.topicName)
                }

                //if subtopic list is not empty initializing the subTopic Adapter and recyclerView
                try {

                    if (it.subTopics == null) {

                        binding.subTopicRV.hide()
                        binding.addSubTopicBtn.show()
                    } else {

                        Log.i(TAG, "setData: sub topic is not null")

                        it.subTopics?.let { subTopicList ->

                            if (subTopicList.size <= 0) {

                                Log.i(TAG, "setData: subtopic list is empty")

                                binding.subTopicRV.hide()
                                binding.addSubTopicBtn.show()
                            } else {

                                Log.i(TAG, "setData: subtopic list is not empty")

                                binding.subTopicRV.show()
                                binding.addSubTopicBtn.hide()

                                subTopicAdapter = SubTopicAdapter()

                                val subTopicRecyclerViewLayoutManager = LinearLayoutManager(binding.subTopicRV.context)
                                subTopicRecyclerViewLayoutManager.initialPrefetchItemCount = subTopicList.size

                                subTopicAdapter.submitList(subTopicList)

                                binding.subTopicRV.apply {

                                    setHasFixedSize(true)
                                    adapter = subTopicAdapter
                                    layoutManager = subTopicRecyclerViewLayoutManager
                                }

                                binding.subTopicRV.setRecycledViewPool(viewPool)

                                subTopicAdapter.setOnClickListener(this)
                            }
                        }
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }

            }
        }


        init {

            binding.addSubTopicBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onAddSubTopicClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                }

                removeTheFocus()
            }

            binding.addAnotherTopicBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.addOnTopicButtonClicked(absoluteAdapterPosition)

                }

                removeTheFocus()
            }

            binding.clearTopicButton.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onClearTopicButtonClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                }

                removeTheFocus()
            }

            binding.checkBoxTopicName.setOnCheckedChangeListener { _, isChecked ->

                try {

                    if (binding.etTopicName.text.toString().trim().isNotEmpty()) {

                        if (checkForNullability(absoluteAdapterPosition)) {

                            mListener!!.onTopicCheckChanged(getItem(absoluteAdapterPosition), absoluteAdapterPosition, isChecked)
                        }
                    } else {

                        binding.checkBoxTopicName.isChecked = false
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }

                removeTheFocus()
            }

            var job: Job? = null
            binding.etTopicName.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    try {

                        if (job != null && job?.isActive == true) {

                            Log.d(TAG, "onTextChanged: Cancelling job")
                            job!!.cancel()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {

                        job = GlobalScope.launch {

                            delay(300)

                            withContext(Dispatchers.Main) {

                                if (checkForNullability(absoluteAdapterPosition)) {

                                    mListener!!.onTopicNameChanged(
                                            s.toString().trim(),
                                            absoluteAdapterPosition,
                                            getItem(absoluteAdapterPosition)
                                    )
                                }
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

        private fun removeTheFocus() {

            try {

                binding.etTopicName.clearFocus()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }


        //on subTopic click listener
        override fun onItemClick(subTopic: SubTopic) {

            if (checkForNullability(absoluteAdapterPosition)) {

                mListener!!.onSubTopicClick(subTopic)
            }
        }

        override fun onAddAnotherSubTopicClicked(subTopicPosition: Int) {

            if (checkForNullability(absoluteAdapterPosition)) {

                mListener!!.onAddSubTopicBtnClickedBySubTopicAdapter(
                        getItem(absoluteAdapterPosition),
                        absoluteAdapterPosition,
                        subTopicPosition
                )
            }
        }

        override fun onSubTopicCheckChanged(subTopic: SubTopic?, position: Int, isChecked: Boolean) {

            if (checkForNullability(absoluteAdapterPosition)) {

                mListener!!.onSubTopicCheckChanged(
                        isChecked,
                        absoluteAdapterPosition,
                        position
                )
            }
        }

        override fun onSubTopicNameChanged(subTopicName: String, subTopicPosition: Int) {

            if (checkForNullability(absoluteAdapterPosition)) {

                mListener!!.onSubTopicNameChanged(
                        subTopicName,
                        absoluteAdapterPosition,
                        subTopicPosition
                )
            }
        }

        override fun onDeleteSubTopicClicked(subTopic: SubTopic, subTopicPosition: Int) {

            if (checkForNullability(absoluteAdapterPosition)) {

                mListener!!.onDeleteSubTopicClicked(
                        subTopic,
                        absoluteAdapterPosition,
                        subTopicPosition
                )
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Topic>() {

        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem.topicKey == newItem.topicKey

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {

        context = parent.context

        val binding = AdapterTopicLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

        holder.setData(getItem(position))

/*
        if (position >= itemCount - 1) {

            holder.binding.addAnotherTopicBtn.show()
        } else {

            holder.binding.addAnotherTopicBtn.hide()
        }
*/

    }

    interface OnClickListener {

        //topic functions
        fun onItemClick(topic: Topic)
        fun addOnTopicButtonClicked(position: Int)
        fun onClearTopicButtonClicked(topic: Topic, position: Int)
        fun onTopicCheckChanged(topic: Topic, position: Int, isChecked: Boolean)
        fun onTopicNameChanged(topicName: String, position: Int, topic: Topic)
        fun onAddSubTopicClicked(topic: Topic, position: Int)

        //subtopic functions
        fun onSubTopicClick(subTopic: SubTopic)
        fun onAddSubTopicBtnClickedBySubTopicAdapter(topic: Topic, position: Int, subTopicPosition: Int)
        fun onSubTopicCheckChanged(isChecked: Boolean, topicPosition: Int, subTopicPosition: Int)
        fun onSubTopicNameChanged(subTopicName: String, topicPosition: Int, subTopicPosition: Int)
        fun onDeleteSubTopicClicked(subTopic: SubTopic, topicPosition: Int, subTopicPosition: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
