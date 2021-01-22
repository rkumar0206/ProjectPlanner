package com.rohitthebest.projectplanner.ui.adapters

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants
import com.rohitthebest.projectplanner.databinding.AdapterTopicLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import kotlinx.coroutines.*

class TopicAdapter : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    private var viewPool = RecyclerView.RecycledViewPool()

    private lateinit var context: Context

    private lateinit var subTopicAdapter: SubTopicAdapter

    inner class TopicViewHolder(val binding: AdapterTopicLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(topic: Topic?) {

            topic?.let {

                binding.etTopicName.setText(it.topicName)

                val spannableStringBuilder = SpannableStringBuilder(it.topicName)
                val strikeThroughSpan = StrikethroughSpan()

                binding.checkBoxTopicName.isChecked = it.isCompleted == Constants.TRUE

                //checking if the topic is completed
                if (it.topicName.isNotEmpty() && it.isCompleted == Constants.TRUE) {

                    binding.checkBoxTopicName.isChecked = true

                    spannableStringBuilder.setSpan(
                            strikeThroughSpan,
                            0,
                            it.topicName.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    binding.etTopicName.text = spannableStringBuilder
                }

                try {

                    it.subTopics?.let { subTopicList ->

                        if (subTopicList.size <= 0) {

                            binding.subTopicRV.hide()
                            binding.addSubTopicBtn.show()
                        } else {

                            binding.subTopicRV.show()
                            binding.addSubTopicBtn.hide()

                            val managerLayout = LinearLayoutManager(binding.subTopicRV.context)
                            managerLayout.initialPrefetchItemCount = subTopicList.size

                            subTopicAdapter.submitList(subTopicList)

                            binding.subTopicRV.apply {

                                setHasFixedSize(true)
                                adapter = subTopicAdapter
                                layoutManager = managerLayout
                            }

                            binding.subTopicRV.setRecycledViewPool(viewPool)
                        }
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }

            }

            if (absoluteAdapterPosition == itemCount - 1) {

                binding.addAnotherTopicBtn.show()
            } else {

                binding.addAnotherTopicBtn.hide()
            }

        }


        init {

            subTopicAdapter = SubTopicAdapter()

            binding.addSubTopicBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onAddSubTopicClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                }
            }

            var job: Job? = null

            binding.addAnotherTopicBtn.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    if (binding.etTopicName.text.toString().trim().isNotEmpty()) {

                        mListener!!.addOnTopicButtonClicked()
                    } else {

                        binding.etTopicName.requestFocus()
                        binding.etTopicName.error = "Specify this topic before adding another topic!!!"
                    }
                }
            }

            binding.clearTopicButton.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onClearTopicButtonClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                }
            }

            binding.checkBoxTopicName.setOnCheckedChangeListener { buttonView, isChecked ->

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
            }

            binding.etTopicName.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    try {

                        if (job != null && job?.isActive == true) {

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

            binding.etTopicName.setOnFocusChangeListener { v, hasFocus ->

                try {
                    if (!hasFocus) {

                        notifyItemChanged(absoluteAdapterPosition)
                    }
                } catch (e: IllegalStateException) {

                    e.printStackTrace()
                }
            }
        }

        fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
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
    }

    interface OnClickListener {

        fun onItemClick(topic: Topic)

        fun addOnTopicButtonClicked()

        fun onClearTopicButtonClicked(topic: Topic, position: Int)

        fun onTopicCheckChanged(topic: Topic, position: Int, isChecked: Boolean)

        fun onTopicNameChanged(topicName: String, position: Int, topic: Topic)

        fun onAddSubTopicClicked(topic: Topic, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
