package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentHomeBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.adapters.ProjectAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), ProjectAdapter.OnClickListener, View.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: ProjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        mAdapter = ProjectAdapter()

        showProgressBar()

        GlobalScope.launch {

            delay(250)

            withContext(Dispatchers.Main) {

                getProjectList()
            }
        }
        initListeners()
    }

    private fun initListeners() {

        binding.addProjectButton.setOnClickListener(this)

        binding.rvProjects.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                try {

                    if (dy > 0 && binding.addProjectButton.visibility == View.VISIBLE) {

                        binding.addProjectButton.hide()
                    } else if (dy < 0 && binding.addProjectButton.visibility != View.VISIBLE) {

                        binding.addProjectButton.show()
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }
        })
    }


    override fun onClick(v: View?) {

        when (v?.id) {

            binding.addProjectButton.id -> {

                findNavController().navigate(R.id.action_homeFragment_to_addEditProjectFragment)
            }

        }
    }

    private fun getProjectList() {

        try {
            projectViewModel.projects.observe(viewLifecycleOwner) {

                setUpRecyclerView(it)
            }
        } catch (e: java.lang.IllegalStateException) {

            e.printStackTrace()
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

            hideProgressBar()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgressBar() {

        try {

            binding.progressBar.show()
        } catch (e: IllegalStateException) {

            e.printStackTrace()
        }
    }

    private fun hideProgressBar() {

        try {

            binding.progressBar.hide()
        } catch (e: IllegalStateException) {

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