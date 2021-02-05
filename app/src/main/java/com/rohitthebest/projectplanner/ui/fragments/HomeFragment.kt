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
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentHomeBinding
import com.rohitthebest.projectplanner.db.entity.Feature
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Technology
import com.rohitthebest.projectplanner.db.entity.Url
import com.rohitthebest.projectplanner.ui.adapters.*
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.ui.viewModels.TaskViewModel
import com.rohitthebest.projectplanner.utils.*
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.openLinkInBrowser
import com.rohitthebest.projectplanner.utils.Functions.Companion.shareAsText
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.ProjectHelperFunctions.Companion.showBottomSheetDialogForAddingFeature
import com.rohitthebest.projectplanner.utils.ProjectHelperFunctions.Companion.showBottomSheetDialogForAddingLinkResource
import com.rohitthebest.projectplanner.utils.ProjectHelperFunctions.Companion.showBottomSheetDialogForAddingTechnology
import com.rohitthebest.projectplanner.utils.ProjectHelperFunctions.Companion.showDialogForAddingSkills
import com.rohitthebest.projectplanner.utils.converters.GsonConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    View.OnClickListener, ProjectAdapter.OnClickListener,
    LinkResourceAdapter.OnClickListener,
    FeatureAdapter.OnClickListener,
    StringAdapter.OnClickListener,
    TechnologyAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()
    private val taskViewModel by viewModels<TaskViewModel>()
    private val bugViewModel by viewModels<BugViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var projectAdapter: ProjectAdapter

    private var projectList: List<Project>? = null

    private lateinit var classForAddingProject: ClassForAddingProject

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        initListeners()

        projectAdapter = ProjectAdapter(taskViewModel, bugViewModel, viewLifecycleOwner)

        showProgressBar()

        GlobalScope.launch {

            delay(250)

            withContext(Dispatchers.Main) {

                getProjectList()
            }
        }

        classForAddingProject = ClassForAddingProject(
                requireContext(),
                projectViewModel
        )

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

    private var clickedProject: Project? = null
    private var openedDialog: MaterialDialog? = null

    /**[START OF PROJECT ADAPTER CLICK LISTENERS] **/

    //-------------------------------FEATURE----------------------------------
    override fun onFeatureClicked(project: Project) {

        val featureList = project.features

        val featureAdapter = FeatureAdapter()

        if (featureList.isEmpty()) {

            "You haven't added any features to this project yet".showToasty(
                requireContext(),
                ToastyType.INFO,
                true,
                Toast.LENGTH_LONG
            )
        } else {

            clickedProject = project

            featureAdapter.submitList(featureList)

            featureAdapter.setOnClickListener(this)

            openedDialog = MaterialDialog(requireContext(), BottomSheet()).show {

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

    override fun onFeatureClicked(feature: Feature, position: Int) {

        showBottomSheetDialogForAddingFeature(
                classForAddingProject,
                clickedProject!!,
                feature,
                position
        )

        openedDialog?.dismiss()
    }

    override fun onDeleteFeatureBtnClicked(feature: Feature, position: Int) {

        clickedProject!!.features.remove(feature)
        projectViewModel.updateProject(clickedProject!!)

        openedDialog?.dismiss()

        Snackbar.make(binding.root, "Feature deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {

                clickedProject!!.features.add(position, feature)

                projectViewModel.updateProject(clickedProject!!)
            }
            .show()
    }

    override fun onAddFeatureBtnClicked(project: Project) {

        showBottomSheetDialogForAddingFeature(
            classForAddingProject,
            project,
            position = if (project.features.size == 0) {
                0
            } else {
                project.features.lastIndex + 1
            }
        )
    }
    //------------------------------------------------------------------------


    //----------------------------------SKILL-------------------------------------
    override fun onSkillClicked(project: Project) {

        clickedProject = project

        val skillList = project.skillsRequired

        val skillAdapter = StringAdapter()

        if (skillList.isEmpty()) {

            "You haven't added any skills required to this project yet".showToasty(
                requireContext(),
                ToastyType.INFO,
                true,
                Toast.LENGTH_LONG
            )

        } else {

            skillAdapter.submitList(skillList)

            skillAdapter.setOnClickListener(this)

            openedDialog = MaterialDialog(requireContext(), BottomSheet()).show {

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

    override fun onStringClicked(text: String?, position: Int) {

        showDialogForAddingSkills(
                classForAddingProject,
                text,
                clickedProject!!,
                position
        )

        openedDialog?.dismiss()
    }

    override fun onDeleteStringClicked(text: String, position: Int) {

        clickedProject!!.skillsRequired.remove(text)
        projectViewModel.updateProject(clickedProject!!)

        openedDialog?.dismiss()

        Snackbar.make(binding.root, "Skill deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {

                clickedProject!!.skillsRequired.add(position, text)

                projectViewModel.updateProject(clickedProject!!)
            }
            .show()
    }

    override fun onAddSkillBtnClicked(project: Project) {

        showDialogForAddingSkills(
            classForAddingProject,
            project = project,
            position = if (project.skillsRequired.size == 0) {
                0
            } else {

                project.skillsRequired.lastIndex + 1
            }
        )
    }
    //-----------------------------------------------------------------------------


    //--------------------------------TECHNOLOGY------------------------------------
    override fun onTechnologyClicked(project: Project) {

        clickedProject = project

        val technologyList = project.technologyUsed

        val technologyAdapter = TechnologyAdapter()

        if (technologyList.isEmpty()) {

            "You haven't added any technology used in this project yet".showToasty(
                requireContext(),
                ToastyType.INFO,
                true,
                Toast.LENGTH_LONG
            )

        } else {

            technologyAdapter.submitList(technologyList)
            technologyAdapter.setOnClickListener(this)

            openedDialog = MaterialDialog(requireContext(), BottomSheet()).show {

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

    override fun onTechnologyClicked(technology: Technology, position: Int) {

        showBottomSheetDialogForAddingTechnology(
                classForAddingProject,
                clickedProject!!,
                technology,
                position
        )

        openedDialog?.dismiss()
    }

    override fun onTechnologyDeleteBtnClicked(technology: Technology, position: Int) {

        clickedProject!!.technologyUsed.remove(technology)
        projectViewModel.updateProject(clickedProject!!)

        openedDialog?.dismiss()

        Snackbar.make(binding.root, "Technology deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {

                clickedProject!!.technologyUsed.add(position, technology)
                projectViewModel.updateProject(clickedProject!!)

            }
            .show()
    }

    override fun onAddTechnologyBtnClicked(project: Project) {

        showBottomSheetDialogForAddingTechnology(
            classForAddingProject,
            project,
            position = if (project.technologyUsed.size == 0) {
                0
            } else {

                project.technologyUsed.lastIndex + 1
            }
        )
    }
    //-----------------------------------------------------------------------------


    //----------------------------------RESOURCES----------------------------------
    override fun onResourcesClicked(project: Project) {

        clickedProject = project

        val linkResourceList = project.resources?.urls

        val linkAdapter = LinkResourceAdapter()

        if (linkResourceList?.isEmpty() == true) {

            "You haven't added any resource to this project yet".showToasty(
                requireContext(),
                ToastyType.INFO,
                true,
                Toast.LENGTH_LONG
            )

        } else {

            linkAdapter.submitList(linkResourceList)
            linkAdapter.setOnClickListener(this)

            openedDialog = MaterialDialog(requireContext(), BottomSheet()).show {

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

    override fun onLinkClick(link: Url) {

        if (isInternetAvailable(requireContext())) {

            openLinkInBrowser(link.url, requireContext())
        } else {

            showNoInternetMessage(requireContext())
        }
    }

    override fun onEditLinkButtonClicked(link: Url, position: Int) {

        showBottomSheetDialogForAddingLinkResource(
                classForAddingProject,
                clickedProject!!,
                link,
                position
        )

        openedDialog?.dismiss()
    }

    override fun onDeleteLinkClicked(link: Url, position: Int) {

        clickedProject!!.resources?.urls?.remove(link)
        projectViewModel.updateProject(clickedProject!!)

        openedDialog?.dismiss()

        Snackbar.make(binding.root, "Link deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    clickedProject!!.resources?.urls?.add(position, link)
                    projectViewModel.updateProject(clickedProject!!)

                }
                .show()

    }

    override fun onShareLinkBtnClicked(url: String) {

        shareAsText(
                url,
                "URL",
                requireContext()
        )
    }

    override fun onAddResourceBtnClicked(project: Project) {

        showBottomSheetDialogForAddingLinkResource(
            classForAddingProject,
            project,
            position = if (
                project.resources?.urls?.size == 0
            ) {
                0
            } else {
                project.resources?.urls?.lastIndex?.plus(1)!!
            }
        )
    }
    //-------------------------------------------------------------------------------


    override fun onTaskBtnClicked(projectKey: String) {

        val action = HomeFragmentDirections.actionHomeFragmentToTaskFragment(
            projectKey
        )

        findNavController().navigate(action)
    }

    override fun onBugFixBtnClicked(projectKey: String) {

        val action = HomeFragmentDirections.actionHomeFragmentToBugFragment(
                projectKey
        )

        findNavController().navigate(action)

    }

    override fun onShareBtnClicked(project: Project) {
        //TODO("Not yet implemented")

        val projectAsString: StringBuilder = StringBuilder()

        val message = "--------------------------- ${project.description.name} ---------------------------" +
                "\n\nProject Description : ${project.description.desc}"

        projectAsString.append(message)

        if (project.features.size != 0) {

            projectAsString.append("\n\n******** Features ********")

            for (i in 0 until project.features.size) {

                val feature = project.features[i]

                val s = "\n\n${i + 1}. ${feature.name}\nDescription : ${feature.description}\n" +
                        "Implementation : ${feature.implementation}\n-------------------------------"

                projectAsString.append(s)
            }
        }

        if (project.skillsRequired.size != 0) {

            projectAsString.append("\n\n******** Skills Required ********")

            for (i in 0 until project.skillsRequired.size) {

                val skill = project.skillsRequired[i]

                val s = "\n\n${i + 1}. ${skill}\n-------------------------------"

                projectAsString.append(s)
            }
        }

        if (project.technologyUsed.size != 0) {

            projectAsString.append("\n\n******** Technology used ********")

            for (i in 0 until project.technologyUsed.size) {

                val technology = project.technologyUsed[i]

                val s = "\n\n${i + 1}. ${technology.name}\n-------------------------------"

                projectAsString.append(s)
            }
        }

        if (project.resources?.urls?.size != 0) {

            projectAsString.append("\n\n******** Resources ********")

            for (i in 0 until project.resources?.urls?.size!!) {

                val url = project.resources?.urls!![i]

                val s = "\n\n${i + 1}. ${url.urlName}\n ${url.url}\n-------------------------------"

                projectAsString.append(s)
            }
        }

        shareAsText(
                "$projectAsString",
                project.description.name,
                requireContext()
        )
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

    /**[END OF PROJECT ADAPTER CLICK LISTENERS] **/

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

                        projectAdapter = ProjectAdapter(taskViewModel, bugViewModel, viewLifecycleOwner)

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