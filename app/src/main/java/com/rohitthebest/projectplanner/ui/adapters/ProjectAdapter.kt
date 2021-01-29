package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterProjectLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.utils.WorkingWithDateAndTime
import com.rohitthebest.projectplanner.utils.hide

class ProjectAdapter : ListAdapter<Project, ProjectAdapter.ProjectViewModel>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ProjectViewModel(val binding: AdapterProjectLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(project: Project?) {

            project?.let {

                binding.apply {

                    projectNameTV.text = it.description.name

                    if (it.description.desc == "") {

                        projectDescriptionTV.hide()
                    } else {

                        projectDescriptionTV.text = it.description.desc
                    }

                    projectModifiedOnTV.text = WorkingWithDateAndTime().convertMillisecondsToDateAndTimePattern(it.modifiedOn)

                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Project>() {

        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem.projectKey == newItem.projectKey

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewModel {

        val binding = AdapterProjectLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectViewModel(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewModel, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(project: Project)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
