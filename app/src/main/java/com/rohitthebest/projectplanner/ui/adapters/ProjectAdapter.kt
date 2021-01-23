package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterProjectsLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Project

class ProjectAdapter : ListAdapter<Project, ProjectAdapter.ProjectViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ProjectViewHolder(val binding: AdapterProjectsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(project: Project?) {

            binding.projectNameTV.text = project?.projectName
        }

        init {

            binding.root.setOnClickListener {

                mListener!!.onItemClick(getItem(absoluteAdapterPosition))
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Project>() {

        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem.projectKey == newItem.projectKey

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {

        val binding = AdapterProjectsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(project: Project)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
