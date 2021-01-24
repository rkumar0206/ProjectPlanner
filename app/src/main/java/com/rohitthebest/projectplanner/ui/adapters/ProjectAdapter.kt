package com.rohitthebest.projectplanner.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.databinding.AdapterProjectsLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.utils.Functions.Companion.setDateInTextView

class ProjectAdapter : ListAdapter<Project, ProjectAdapter.ProjectViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ProjectViewHolder(val binding: AdapterProjectsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setData(project: Project?) {

            project?.let {

                binding.projectNameTV.text = it.projectName
                binding.projectStartedOnTV.setDateInTextView(it.timeStamp, "dd-MM-yyyy", "Started On  :  ")
                binding.projectModifiedOnTV.setDateInTextView(it.modifiedOn, "dd-MM-yyyy", "Modified On:  ")
                binding.projectProgressPB.max = 100
                binding.projectProgressPB.progress = it.projectProgress
                binding.projectProgressTV.text = "${it.projectProgress}%"


            }
        }

        init {

            binding.projectAdapterRoot.setOnClickListener {

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
