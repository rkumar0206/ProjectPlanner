package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.databinding.AdapterTaskLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Task
import com.rohitthebest.projectplanner.utils.strikeThrough

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class TaskViewHolder(val binding: AdapterTaskLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(task: Task?) {

            task?.let {

                if (it.isCompleted == TRUE) {

                    binding.checkBoxTaskName.isChecked = true
                    binding.checkBoxTaskName.strikeThrough(it.taskName)
                }
                binding.checkBoxTaskName.text = it.taskName
            }
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

        fun onItemClick(task: Task)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
