package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentHomeBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.adapters.ProjectAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), ProjectAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: ProjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        mAdapter = ProjectAdapter()

        binding.addProjectButton.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_addEditProjectFragment)
        }

        getProjectList()
    }

    private fun getProjectList() {

        projectViewModel.projects.observe(viewLifecycleOwner) {

            setUpRecyclerView(it)
        }
    }

    private fun setUpRecyclerView(projectList: List<Project>?) {

        try {

            mAdapter.submitList(projectList)

            binding.rvProjects.apply {

                setHasFixedSize(true)
                adapter = mAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            mAdapter.setOnClickListener(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(project: Project) {

        Log.i(TAG, "onItemClick: $project")

        showToast(requireContext(), "$project")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}