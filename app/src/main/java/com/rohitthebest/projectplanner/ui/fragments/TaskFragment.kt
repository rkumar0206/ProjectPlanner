package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentTaskBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.ui.viewModels.TaskViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.hideKeyBoard
import com.rohitthebest.projectplanner.utils.hideViewBySlidingAnimation
import com.rohitthebest.projectplanner.utils.showViewBySlidingAnimation
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TaskFragment"

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task), View.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()
    private val taskViewModel by viewModels<TaskViewModel>()

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var project: Project

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskBinding.bind(view)

        initListeners()

        getMessage()
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    TaskFragmentArgs.fromBundle(it)
                }

                val projectKey = args?.message

                getProjectFromDatabase(projectKey!!)

                getTasksFromProjectKey(projectKey)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTasksFromProjectKey(projectKey: String) {

        try {

            taskViewModel.getAllTaskByProjectKey(projectKey).observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {

                    binding.addFirstTaskBtn.hideViewBySlidingAnimation(
                            View.GONE
                    )
                } else {

                    binding.addFirstTaskBtn.showViewBySlidingAnimation()
                }

                //todo : setUpTaskRecyclerView
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getProjectFromDatabase(projectKey: String) {

        try {

            projectViewModel.getProjectByProjectKey(projectKey).observe(viewLifecycleOwner) {

                if (it != null) {

                    project = it

                    Log.d(TAG, "getProjectFromDatabase: $project")

                    updateUi()

                    //todo : get tasks from this project key
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUi() {

        binding.tvProjectName.text = project.description.name
    }

    private fun initListeners() {

        binding.addFirstTaskBtn.setOnClickListener(this)
        binding.addTaskIB.setOnClickListener(this)

    }

    override fun onClick(v: View?) {


    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideKeyBoard(requireActivity())

        _binding = null
    }
}
