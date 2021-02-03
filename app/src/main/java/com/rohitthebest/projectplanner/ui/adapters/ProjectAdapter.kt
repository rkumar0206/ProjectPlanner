package com.rohitthebest.projectplanner.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.databinding.AdapterProjectLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.ui.viewModels.TaskViewModel
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.setDateInTextView
import com.rohitthebest.projectplanner.utils.show

class ProjectAdapter(
        val taskViewModel: TaskViewModel,
        val bugViewModel: BugViewModel,
        val lifeCycleOwner: LifecycleOwner
) : ListAdapter<Project, ProjectAdapter.ProjectViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ProjectViewHolder(val binding: AdapterProjectLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        @SuppressLint("SetTextI18n")
        fun setData(project: Project?) {

            project?.let {

                binding.apply {

                    projectNameTV.text = it.description.name

                    if (it.description.desc == "") {

                        projectDescriptionTV.hide()
                    } else {

                        projectDescriptionTV.show()
                        projectDescriptionTV.text = it.description.desc
                    }

                    projectModifiedOnTV.setDateInTextView(
                            it.modifiedOn,
                            startingText = "Modified On : "
                    )

                    projectStartedOnTV.setDateInTextView(
                            it.timeStamp,
                            startingText = "Started On : "
                    )

                    taskViewModel.getAllTaskByProjectKey(it.projectKey).observe(lifeCycleOwner) {

                        if (it.isNotEmpty()) {

                            val inCompleted = it.filter { t ->

                                t.isCompleted != TRUE
                            }

                            when {
                                inCompleted.size in 1..99 -> {

                                    numberOfTasksCV.show()
                                    numberOfTasksTV.text = "${inCompleted.size}"
                                }
                                inCompleted.size > 99 -> {

                                    numberOfTasksCV.show()
                                    numberOfTasksTV.text = "99+"
                                }
                                else -> {
                                    numberOfTasksCV.hide()
                                }
                            }

                        }
                    }

                    bugViewModel.getAllBugsByProjectKey(it.projectKey).observe(lifeCycleOwner) {

                        if (it.isNotEmpty()) {

                            val notResolvedList = it.filter { b ->
                                b.isResolved != TRUE
                            }

                            when {
                                notResolvedList.size in 1..99 -> {

                                    noOfBugsCV.show()
                                    numberOfBugsTV.text = "${notResolvedList.size}"
                                }
                                notResolvedList.size > 99 -> {

                                    noOfBugsCV.show()
                                    numberOfBugsTV.text = "99+"
                                }
                                else -> {
                                    noOfBugsCV.hide()
                                }
                            }

                        }
                    }

                    setUpThemesColor(it)
                }
            }
        }

        init {

            binding.projectAdapterRootLayout.setOnClickListener(this)

            binding.featuresIconIV.setOnClickListener(this)
            binding.skillsIconIV.setOnClickListener(this)
            binding.technologyIconIV.setOnClickListener(this)
            binding.resourcesIconIV.setOnClickListener(this)

            binding.addTasksToProjectBtn.setOnClickListener(this)
            binding.addBugsToTheProjectBtn.setOnClickListener(this)
            binding.uploadProjectToCloudBtn.setOnClickListener(this)
            binding.deleteProjectBtn.setOnClickListener(this)

            binding.addFeatureCV.setOnClickListener(this)
            binding.addSkillCV.setOnClickListener(this)
            binding.addTechnologyCV.setOnClickListener(this)
            binding.addResourceCV.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability(absoluteAdapterPosition)) {

                when (v?.id) {

                    binding.projectAdapterRootLayout.id -> {

                        mListener!!.onItemClick(getItem(absoluteAdapterPosition))
                    }

                    binding.featuresIconIV.id -> {

                        mListener!!.onFeatureClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.skillsIconIV.id -> {

                        mListener!!.onSkillClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.technologyIconIV.id -> {

                        mListener!!.onTechnologyClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.resourcesIconIV.id -> {

                        mListener!!.onResourcesClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.addTasksToProjectBtn.id -> {

                        mListener!!.onTaskBtnClicked(getItem(absoluteAdapterPosition).projectKey)
                    }
                    binding.addBugsToTheProjectBtn.id -> {

                        mListener!!.onBugFixBtnClicked(getItem(absoluteAdapterPosition).projectKey)
                    }
                    binding.uploadProjectToCloudBtn.id -> {

                        mListener!!.onUploadBtnClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.deleteProjectBtn.id -> {

                        mListener!!.onDeleteProjectBtnClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.addFeatureCV.id -> {

                        mListener!!.onAddFeatureBtnClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.addSkillCV.id -> {

                        mListener!!.onAddSkillBtnClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.addTechnologyCV.id -> {

                        mListener!!.onAddTechnologyBtnClicked(getItem(absoluteAdapterPosition))
                    }
                    binding.addResourceCV.id -> {

                        mListener!!.onAddResourceBtnClicked(getItem(absoluteAdapterPosition))
                    }
                }
            }
        }


        private fun setUpThemesColor(project: Project) {

            try {

                binding.primaryColorView.setBackgroundColor(Color.parseColor(project.theme?.primaryColor))
                binding.primaryColorDarkView.setBackgroundColor(Color.parseColor(project.theme?.darkPrimaryColor))
                binding.accentColorView.setBackgroundColor(Color.parseColor(project.theme?.accentColor))
                binding.primaryTextColorView.setBackgroundColor(Color.parseColor(project.theme?.primaryTextColor))
                binding.secondaryTextColorView.setBackgroundColor(Color.parseColor(project.theme?.secondaryTextColor))
                binding.textOnPrimaryColorView.setBackgroundColor(Color.parseColor(project.theme?.textColorOnPrimaryColor))

            } catch (e: StringIndexOutOfBoundsException) {

                e.printStackTrace()
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Project>() {

        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem.projectKey == newItem.projectKey

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {

        val binding = AdapterProjectLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(project: Project)

        fun onFeatureClicked(project: Project)
        fun onSkillClicked(project: Project)
        fun onTechnologyClicked(project: Project)
        fun onResourcesClicked(project: Project)

        fun onTaskBtnClicked(projectKey: String)
        fun onBugFixBtnClicked(projectKey: String)
        fun onUploadBtnClicked(project: Project)
        fun onDeleteProjectBtnClicked(project: Project)

        fun onAddFeatureBtnClicked(project: Project)
        fun onAddSkillBtnClicked(project: Project)
        fun onAddTechnologyBtnClicked(project: Project)
        fun onAddResourceBtnClicked(project: Project)

    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
