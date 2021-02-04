package com.rohitthebest.projectplanner.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AdapterTaskLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Task
import com.rohitthebest.projectplanner.utils.*

private const val TAG = "TaskAdapter"

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class TaskViewHolder(val binding: AdapterTaskLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun setData(task: Task?) {

            task?.let {

                if (it.isCompleted) {

                    Log.d(TAG, "setData: TRUE")

                    binding.checkBoxTaskName.isChecked = true
                    binding.editTaskButton.hide()
                    binding.labelAsImportantBtn.hide()
                    binding.checkBoxTaskName.strikeThrough(it.taskName)
                    binding.checkBoxTaskName.changeTextColor(
                            itemView.context,
                            R.color.secondary_text_color
                    )
                } else {

                    Log.d(TAG, "setData: FALSE")

                    binding.editTaskButton.show()
                    binding.labelAsImportantBtn.show()
                    binding.checkBoxTaskName.isChecked = false
                    binding.checkBoxTaskName.text = it.taskName
                    binding.checkBoxTaskName.changeTextColor(itemView.context, R.color.primary_text_color)
                }

                if (it.isImportant) {

                    binding.labelAsImportantBtn.setImageResource(R.drawable.ic_baseline_label_important_24)
                } else {

                    binding.labelAsImportantBtn.setImageResource(R.drawable.ic_important_outline)
                }

                binding.taskAddedOnTV.setDateInTextView(
                        it.timeStamp,
                        "dd-MM-yyyy, hh:mm a",
                        "Added On : "
                )
            }
        }

        init {

            binding.checkBoxTaskName.setOnClickListener(this)
            binding.clearTopicButton.setOnClickListener(this)
            binding.editTaskButton.setOnClickListener(this)
            binding.labelAsImportantBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability(absoluteAdapterPosition)) {

                when (v?.id) {

                    binding.checkBoxTaskName.id -> {

                        mListener!!.onCheckChanged(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )

                    }

                    binding.editTaskButton.id -> {

                        mListener!!.onEditTaskClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                    }

                    binding.clearTopicButton.id -> {

                        mListener!!.onDeleteTaskClicked(getItem(
                                absoluteAdapterPosition
                        ), absoluteAdapterPosition)
                    }

                    binding.labelAsImportantBtn.id -> {

                        mListener!!.onLabelAsImportantClicked(
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

    class DiffUtilCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.taskKey == newItem.taskKey

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val binding = AdapterTaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onCheckChanged(task: Task, position: Int)
        fun onEditTaskClicked(task: Task, position: Int)
        fun onDeleteTaskClicked(task: Task, position: Int)
        fun onLabelAsImportantClicked(task: Task, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
