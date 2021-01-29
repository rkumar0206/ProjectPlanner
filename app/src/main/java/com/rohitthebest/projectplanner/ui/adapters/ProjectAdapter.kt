package com.rohitthebest.projectplanner.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterProjectLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.utils.WorkingWithDateAndTime
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show

class ProjectAdapter : ListAdapter<Project, ProjectAdapter.ProjectViewModel>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ProjectViewModel(val binding: AdapterProjectLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

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

                    projectModifiedOnTV.text = "Modified On : " + WorkingWithDateAndTime().convertMillisecondsToDateAndTimePattern(it.modifiedOn)
                    projectStartedOnTV.text = "Started On : " + WorkingWithDateAndTime().convertMillisecondsToDateAndTimePattern(it.timeStamp)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewModel {

        val binding = AdapterProjectLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectViewModel(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewModel, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(project: Project)

        fun onFeatureClicked(project: Project)
        fun onSkillClicked(project: Project)
        fun onTechnologyClicked(project: Project)
        fun onResourcesClicked(project: Project)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
