package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.customListAdapter
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentHomeBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.adapters.FeatureAdapter
import com.rohitthebest.projectplanner.ui.adapters.ProjectAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import com.rohitthebest.projectplanner.utils.converters.GsonConverter
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
        View.OnClickListener, ProjectAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var projectAdapter: ProjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        initListeners()

        projectAdapter = ProjectAdapter()

        showProgressBar()

        GlobalScope.launch {

            delay(250)

            withContext(Dispatchers.Main) {

                getProjectList()
            }
        }
    }

    private fun getProjectList() {

        Log.d(TAG, "getProjectList: ")

        projectViewModel.projects.observe(viewLifecycleOwner) {

            setUpRecyclerView(it)
        }
    }

    private fun setUpRecyclerView(it: List<Project>?) {

        try {

            Log.d(TAG, "setUpRecyclerView: ")

            projectAdapter.submitList(it)

            binding.rvProjects.apply {

                setHasFixedSize(true)
                adapter = projectAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            projectAdapter.setOnClickListener(this)

            hideProgressBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(project: Project) {

        val projectString = GsonConverter().convertProjectToString(project)

        val action = HomeFragmentDirections.actionHomeFragmentToAddEditProjectFragment(
                projectString
        )

        showProgressBar()

        GlobalScope.launch {
            delay(100)

            withContext(Dispatchers.Main) {

                hideProgressBar()

                findNavController().navigate(action)
            }
        }
    }

    override fun onFeatureClicked(project: Project) {

        val featureList = project.features

        val featureAdapter = FeatureAdapter()

        if (featureList.isEmpty()) {

            showToast(requireContext(), "You haven't added any features to this project yet", Toast.LENGTH_LONG)
        } else {

            featureAdapter.submitList(featureList)

            MaterialDialog(requireContext(), BottomSheet()).show {

                title(text = "Features of ${project.description.name}")

                customListAdapter(
                        featureAdapter,
                        LinearLayoutManager(requireContext())
                )
            }
        }
    }

    override fun onSkillClicked(project: Project) {
        //TODO("Not yet implemented")
    }

    override fun onTechnologyClicked(project: Project) {
        //TODO("Not yet implemented")
    }

    override fun onResourcesClicked(project: Project) {
        //TODO("Not yet implemented")
    }

    private fun initListeners() {

        binding.addProjectButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.addProjectButton.id -> {

                findNavController().navigate(R.id.action_homeFragment_to_addEditProjectFragment)
            }

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


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}