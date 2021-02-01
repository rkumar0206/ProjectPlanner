package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.customListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentHomeBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Url
import com.rohitthebest.projectplanner.ui.adapters.*
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.ui.viewModels.TaskViewModel
import com.rohitthebest.projectplanner.utils.Functions
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.openLinkInBrowser
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import com.rohitthebest.projectplanner.utils.converters.GsonConverter
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
        View.OnClickListener, ProjectAdapter.OnClickListener, LinkResourceAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()
    private val taskViewModel by viewModels<TaskViewModel>()
    private val bugViewModel by viewModels<BugViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var projectAdapter: ProjectAdapter

    private var projectList: List<Project>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        initListeners()

        projectAdapter = ProjectAdapter(taskViewModel, viewLifecycleOwner)

        showProgressBar()

        GlobalScope.launch {

            delay(250)

            withContext(Dispatchers.Main) {

                getProjectList()
            }
        }

        setHasOptionsMenu(true)

    }

    private fun getProjectList() {

        Log.d(TAG, "getProjectList: ")

        projectViewModel.projects.observe(viewLifecycleOwner) {

            projectList = it

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

        openProjectFragmentWithProjectAsMessage(project)
    }

    private fun openProjectFragmentWithProjectAsMessage(project: Project) {

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

        val featureAdapter = FeatureAdapter("hide")

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

                positiveButton(text = "Add/Edit feature") {

                    dismiss()

                    openProjectFragmentWithProjectAsMessage(project)
                }
            }
        }
    }

    override fun onSkillClicked(project: Project) {

        val skillList = project.skillsRequired

        val skillAdapter = StringAdapter("hide")

        if (skillList.isEmpty()) {

            showToast(requireContext(), "You haven't added any skills required to this project yet", Toast.LENGTH_LONG)
        } else {

            skillAdapter.submitList(skillList)

            MaterialDialog(requireContext(), BottomSheet()).show {

                title(text = "Skills required in ${project.description.name}")

                customListAdapter(
                        skillAdapter,
                        LinearLayoutManager(requireContext())
                )


                positiveButton(text = "Add/Edit skill") {

                    dismiss()

                    openProjectFragmentWithProjectAsMessage(project)
                }

            }
        }
    }

    override fun onTechnologyClicked(project: Project) {

        val technologyList = project.technologyUsed

        val technologyAdapter = TechnologyAdapter("hide")

        if (technologyList.isEmpty()) {

            showToast(requireContext(), "You haven't added any technology used to this project yet", Toast.LENGTH_LONG)
        } else {

            technologyAdapter.submitList(technologyList)

            MaterialDialog(requireContext(), BottomSheet()).show {

                title(text = "Technologies used in ${project.description.name}")

                customListAdapter(
                        technologyAdapter,
                        LinearLayoutManager(requireContext())
                )

                positiveButton(text = "Add/Edit technologies") {

                    dismiss()
                    openProjectFragmentWithProjectAsMessage(project)
                }

            }
        }
    }

    override fun onResourcesClicked(project: Project) {

        val linkResourceList = project.resources?.urls

        val linkAdapter = LinkResourceAdapter("hide")

        if (linkResourceList?.isEmpty() == true) {

            showToast(requireContext(), "You haven't added any resource to this project yet", Toast.LENGTH_LONG)
        } else {

            linkAdapter.submitList(linkResourceList)

            linkAdapter.setOnClickListener(this)

            MaterialDialog(requireContext(), BottomSheet()).show {

                title(text = "Resources for ${project.description.name}")

                customListAdapter(
                        linkAdapter,
                        LinearLayoutManager(requireContext())
                )

                positiveButton(text = "Add/Edit resource") {

                    dismiss()
                    openProjectFragmentWithProjectAsMessage(project)
                }
            }
        }
    }

    override fun onTaskBtnClicked(projectKey: String) {

        val action = HomeFragmentDirections.actionHomeFragmentToTaskFragment(
                projectKey
        )

        findNavController().navigate(action)
    }

    override fun onAddBugsBtnClicked(projectKey: String) {

        val action = HomeFragmentDirections.actionHomeFragmentToBugFragment(
                projectKey
        )

        findNavController().navigate(action)

    }

    override fun onUploadBtnClicked(project: Project) {
        //TODO("Not yet implemented")
    }

    override fun onDeleteProjectBtnClicked(project: Project) {

        // this is temporary method for deleting , remember to delete this project from cloud
        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete project")
                .setMessage("This project will be no longer available to you.")
                .setPositiveButton("Delete") { dialog, _ ->

                    deleteProject(project)

                    dialog.dismiss()

                    Log.d(TAG, "onDeleteProjectBtnClicked: project deleted")
                    //todo : show snackbar for undoing the delete action
                }
                .setNegativeButton("Cancel") { dialog, _ ->

                    dialog.dismiss()
                }
                .create()
                .show()
    }

    private fun deleteProject(project: Project) {

        projectViewModel.deleteProject(project)

        taskViewModel.deleteTaskByProjectKey(projectKey = project.projectKey)

        bugViewModel.deleteByProjectKey(project.projectKey)
    }

    override fun onLinkClick(link: Url) {

        if (isInternetAvailable(requireContext())) {

            openLinkInBrowser(link.url, requireContext())
        } else {

            showNoInternetMessage(requireContext())
        }
    }

    override fun onEditLinkButtonClicked(link: Url, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onDeleteLinkClicked(link: Url, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onShareLinkBtnClicked(url: String) {

        Functions.shareAsText(
                url,
                "URL",
                requireContext()
        )
    }

    private fun initListeners() {

        binding.addProjectButton.setOnClickListener(this)

        binding.rvProjects.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                try {

                    if (dy > 0 && binding.addProjectButton.visibility == View.VISIBLE) {

                        binding.addProjectButton.hide()
                    } else {

                        if (binding.addProjectButton.visibility != View.VISIBLE) {

                            binding.addProjectButton.show()
                        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search_menu_btn)

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                projectList?.let {

                    if (newText?.trim() == "") {

                        projectAdapter = ProjectAdapter(taskViewModel, viewLifecycleOwner)

                        setUpRecyclerView(it)
                    } else {

                        val filteredList = it.filter { p ->

                            p.description.name.toLowerCase(Locale.ROOT).contains(newText?.trim()?.toLowerCase(Locale.ROOT)!!)
                        }

                        setUpRecyclerView(filteredList)
                    }
                }
                return true
            }
        })
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