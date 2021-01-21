package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.setDateInTextView
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!
    private lateinit var includeBinding: AddEditProjectLayoutBinding

    private var currentTimeStamp = 0L

    private var project: Project? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        currentTimeStamp = System.currentTimeMillis()

        includeBinding.tvProjectDate.setDateInTextView(timeStamp = currentTimeStamp, startingText = "Date : ")

        initListeners()

        observeChanges()
    }

    private fun observeChanges() {

        projectViewModel.projects.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                if (project != null) {

                    projectViewModel.getProjectByProjectKey(project!!.projectKey).observe(viewLifecycleOwner) {


                    }
                }
            }
        }
    }

    private fun initListeners() {

        includeBinding.addTopicBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addTopicBtn.id -> {

                if (includeBinding.etProjectName.text.toString().trim().isNotEmpty()) {

                    //todo : Add Project and hide
                } else {

                    includeBinding.etProjectName.requestFocus()
                    includeBinding.etProjectName.error = "You must give a name to your project."
                }
            }
        }
    }

    private fun showAddBtnAndHideRV() {

        try {

            includeBinding.addTopicBtn.show()
            includeBinding.rvProjectTopic.hide()
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun hideAddBtnAndShowRV() {

        try {

            includeBinding.addTopicBtn.hide()
            includeBinding.rvProjectTopic.show()
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}