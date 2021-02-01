package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentBugBinding
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BugFragment"

@AndroidEntryPoint
class BugFragment : Fragment(R.layout.fragment_bug) {

    private val bugViewModel by viewModels<BugViewModel>()
    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentBugBinding? = null
    private val binding get() = _binding!!

    private var project: Project? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBugBinding.bind(view)

        getMessage()

        binding.addBugBtn.setOnClickListener {

            //todo : add bug
        }
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    BugFragmentArgs.fromBundle(it)
                }

                val projectKey = args?.projectMessage

                getProjectFromProjectKey(projectKey!!)

                getBugsListFromProjectKey(projectKey)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getBugsListFromProjectKey(projectKey: String) {

        bugViewModel.getAllBugsByProjectKey(projectKey).observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                binding.noBugReportFoundTV.hide()
            } else {

                binding.noBugReportFoundTV.show()
            }

            setUpBugRecyclerView(it)
        }
    }

    private fun setUpBugRecyclerView(bugList: List<Bug>?) {

        try {

            //todo : set up the recycler view
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun getProjectFromProjectKey(projectKey: String) {

        projectViewModel.getProjectByProjectKey(projectKey).observe(viewLifecycleOwner) {

            if (it != null) {

                project = it

                Log.d(TAG, "getProjectFromProjectKey: $project")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}