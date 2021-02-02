package com.rohitthebest.projectplanner.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.*
import com.rohitthebest.projectplanner.ui.adapters.*
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.*
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.openLinkInBrowser
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.FuntionsForAddingElementsToProject.Companion.openFeatureBottomSheetDialog
import com.rohitthebest.projectplanner.utils.FuntionsForAddingElementsToProject.Companion.showBottomSheetDialogForAddingColor
import com.rohitthebest.projectplanner.utils.FuntionsForAddingElementsToProject.Companion.showBottomSheetDialogForAddingLinkResource
import com.rohitthebest.projectplanner.utils.FuntionsForAddingElementsToProject.Companion.showBottomSheetDialogForAddingTechnology
import com.rohitthebest.projectplanner.utils.FuntionsForAddingElementsToProject.Companion.showDialogForAddingSkills
import com.rohitthebest.projectplanner.utils.converters.GsonConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project),
        View.OnClickListener, FeatureAdapter.OnClickListener, StringAdapter.OnClickListener,
        TechnologyAdapter.OnClickListener, LinkResourceAdapter.OnClickListener, ColorsAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var includeBinding: AddEditProjectLayoutBinding

    private lateinit var project: Project

    //adapters
    private lateinit var featureAdapter: FeatureAdapter
    private lateinit var skillAdapter: StringAdapter
    private lateinit var technologyAdapter: TechnologyAdapter
    private lateinit var linkResourceAdapter: LinkResourceAdapter
    private lateinit var colorsAdapter: ColorsAdapter

    private var isProjectReceivedForEditing = false

    private lateinit var classForAddingProject: ClassForAddingProject

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        getMessage()

        initProject()

        //init adapters
        featureAdapter = FeatureAdapter()
        skillAdapter = StringAdapter()
        technologyAdapter = TechnologyAdapter()
        linkResourceAdapter = LinkResourceAdapter()
        colorsAdapter = ColorsAdapter()

        setUpRecyclerViews()

        observeProject()

        initListeners()
        setHasOptionsMenu(true)
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    AddEditProjectFragmentArgs.fromBundle(it)
                }

                isProjectReceivedForEditing = true

                project = args?.projectMessage?.let { GsonConverter().convertJsonStringToProject(it) }!!

                Log.d(TAG, "getMessage: $project")

                updateUI()

                observeProject()

                GlobalScope.launch {

                    delay(300)

                    withContext(Dispatchers.Main) {

                        setUpRecyclerViews()
                    }
                }

            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUI() {

        includeBinding.apply {

            projectNameET.setText(project.description.name)
            projectDescriptionET.editText?.setText(project.description.desc)

            project.theme?.let {

                themePrimaryColorBtn.setBackgroundColorByHexCode(it.primaryColor)
                themePrimaryColorDarkBtn.setBackgroundColorByHexCode(it.darkPrimaryColor)
                themeAccentColor.setBackgroundColorByHexCode(it.accentColor)
                themePrimaryTextColor.setBackgroundColorByHexCode(it.primaryTextColor)
                themeSecondaryTextColor.setBackgroundColorByHexCode(it.secondaryTextColor)
                themeTextOnPrimaryColorBtn.setBackgroundColorByHexCode(it.textColorOnPrimaryColor)

                themePrimaryColorTV.text = it.primaryColor
                themePrimaryColorDarkTV.text = it.darkPrimaryColor
                themeAccentColorTV.text = it.accentColor
                themePrimaryTextColorTV.text = it.primaryTextColor
                themeSecondaryTextColorTV.text = it.secondaryTextColor
                themeTextOnPrimaryColorTV.text = it.textColorOnPrimaryColor
            }
        }

    }

    private fun observeProject() {

        try {

            projectViewModel.getProjectByProjectKey(project.projectKey).observe(viewLifecycleOwner) {

                Log.d(TAG, "observeProject: ")

                if (it != null) {

                    Log.d(TAG, "observeProject: project is not null")

                    project = it
                    setUpRecyclerViews()
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initProject() {

        project = Project()

        project.projectKey = generateKey()

        classForAddingProject = ClassForAddingProject(
                requireContext(),
                projectViewModel
        )
    }

    //initialising listeners
    private fun initListeners() {

        includeBinding.addColorBtn.setOnClickListener(this)
        includeBinding.addFeatureBtn.setOnClickListener(this)
        includeBinding.addResourceBtn.setOnClickListener(this)
        includeBinding.addSkillBtn.setOnClickListener(this)
        includeBinding.addTechnologyBtn.setOnClickListener(this)
        includeBinding.themeSeeInLayoutBtn.setOnClickListener(this)
        includeBinding.themeAccentColor.setOnClickListener(this)
        includeBinding.themePrimaryColorBtn.setOnClickListener(this)
        includeBinding.themePrimaryColorDarkBtn.setOnClickListener(this)
        includeBinding.themePrimaryTextColor.setOnClickListener(this)
        includeBinding.themeSecondaryTextColor.setOnClickListener(this)
        includeBinding.themeTextOnPrimaryColorBtn.setOnClickListener(this)

        includeBinding.themePrimaryColorTV.setOnClickListener(this)
        includeBinding.themePrimaryColorDarkTV.setOnClickListener(this)
        includeBinding.themeAccentColorTV.setOnClickListener(this)
        includeBinding.themePrimaryTextColorTV.setOnClickListener(this)
        includeBinding.themeSecondaryTextColorTV.setOnClickListener(this)
        includeBinding.themeTextOnPrimaryColorTV.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addFeatureBtn.id -> {

                openFeatureBottomSheetDialog(
                        classForAddingProject = classForAddingProject,
                        project = project,
                        position = if (project.features.size == 0) {
                            0
                        } else {
                            project.features.lastIndex + 1
                        }
                )
            }
            includeBinding.addResourceBtn.id -> {

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
            includeBinding.addSkillBtn.id -> {

                showDialogForAddingSkills(
                        classForAddingProject,
                        position = if (project.skillsRequired.size == 0) {
                            0
                        } else {
                            project.skillsRequired.lastIndex + 1
                        },
                        project = project
                )
            }
            includeBinding.addTechnologyBtn.id -> {

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
            includeBinding.addColorBtn.id -> {

                showBottomSheetDialogForAddingColor(
                        classForAddingProject,
                        project,
                        position = if (project.colors.size == 0) 0 else project.colors.lastIndex + 1
                )
            }

            includeBinding.themePrimaryColorBtn.id -> {

                includeBinding.themePrimaryColorBtn
                        .openColorPicker(
                                requireContext(),
                                Color.parseColor(includeBinding.themePrimaryColorTV.text.toString().trim()),
                                includeBinding.themePrimaryColorTV
                        )
            }
            includeBinding.themePrimaryColorDarkBtn.id -> {

                includeBinding.themePrimaryColorDarkBtn
                        .openColorPicker(
                                requireContext(),
                                Color.parseColor(includeBinding.themePrimaryColorDarkTV.text.toString().trim()),
                                includeBinding.themePrimaryColorDarkTV
                        )
            }
            includeBinding.themeAccentColor.id -> {

                includeBinding.themeAccentColor
                        .openColorPicker(
                                requireContext(),
                                Color.parseColor(includeBinding.themeAccentColorTV.text.toString().trim()),
                                includeBinding.themeAccentColorTV
                        )
            }
            includeBinding.themePrimaryTextColor.id -> {

                includeBinding.themePrimaryTextColor
                        .openColorPicker(requireContext(),
                                Color.parseColor(includeBinding.themePrimaryTextColorTV.text.toString().trim()),
                                includeBinding.themePrimaryTextColorTV
                        )
            }
            includeBinding.themeSecondaryTextColor.id -> {

                includeBinding.themeSecondaryTextColor
                        .openColorPicker(
                                requireContext(),
                                Color.parseColor(includeBinding.themeSecondaryTextColorTV.text.toString().trim()),
                                includeBinding.themeSecondaryTextColorTV
                        )
            }
            includeBinding.themeTextOnPrimaryColorBtn.id -> {

                includeBinding.themeTextOnPrimaryColorBtn
                        .openColorPicker(
                                requireContext(),
                                Color.parseColor(includeBinding.themeTextOnPrimaryColorTV.text.toString().trim()),
                                includeBinding.themeTextOnPrimaryColorTV
                        )
            }

            includeBinding.themeSeeInLayoutBtn.id -> {

                //todo : Select layout and apply these colors
            }

            includeBinding.themePrimaryColorTV.id -> {

                includeBinding.themePrimaryColorBtn
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themePrimaryColorTV
                        )
            }
            includeBinding.themePrimaryColorDarkTV.id -> {

                includeBinding.themePrimaryColorDarkBtn
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themePrimaryColorDarkTV
                        )
            }
            includeBinding.themeAccentColorTV.id -> {

                includeBinding.themeAccentColor
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themeAccentColorTV
                        )
            }
            includeBinding.themePrimaryTextColorTV.id -> {

                includeBinding.themePrimaryTextColor
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themePrimaryTextColorTV
                        )
            }
            includeBinding.themeSecondaryTextColorTV.id -> {

                includeBinding.themeSecondaryTextColor
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themeSecondaryTextColorTV
                        )
            }
            includeBinding.themeTextOnPrimaryColorTV.id -> {

                includeBinding.themeTextOnPrimaryColorBtn
                        .openDialogForWritingHexColor(
                                requireActivity(),
                                includeBinding.themeTextOnPrimaryColorTV
                        )
            }
        }

        includeBinding.projectNameET.removeFocus()
        includeBinding.projectDescriptionET.editText?.removeFocus()
    }


    //setting up all the recycler views
    private fun setUpRecyclerViews() {

        setUpFeaturesRecyclerView()
        setUpSkillsRecyclerView()
        setUpTechnologyRecyclerView()
        setUpLinkResourceRecyclerView()
        setUpColorsRecyclerView()
    }


    /**[START OF FEATURE]*/

    //Feature recycler view
    private fun setUpFeaturesRecyclerView() {

        try {

            featureAdapter.submitList(project.features)

            includeBinding.featureRV.apply {

                setHasFixedSize(true)
                adapter = featureAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            featureAdapter.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //handling the clicks on feature
    override fun onFeatureClicked(feature: Feature, position: Int) {

        openFeatureBottomSheetDialog(
                classForAddingProject = classForAddingProject,
                project = project,
                feature, position)
    }

    /**[END OF FEATURE]**/


    /**[START OF SKILL]**/

    //Skill recycler view
    private fun setUpSkillsRecyclerView() {

        try {

            skillAdapter.submitList(project.skillsRequired)

            includeBinding.skillRV.apply {

                setHasFixedSize(true)
                adapter = skillAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            skillAdapter.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //handling on click on skill texts
    override fun onStringClicked(text: String?, position: Int) {

        showDialogForAddingSkills(
                classForAddingProject = classForAddingProject,
                skill = text,
                position = position,
                project = project
        )
    }

    override fun onDeleteStringClicked(text: String, position: Int) {

        project.skillsRequired.remove(text)

        setUpSkillsRecyclerView()

        Snackbar.make(binding.root, "Skill deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    project.skillsRequired.add(position, text)

                    setUpSkillsRecyclerView()

                }
                .show()
    }

    /**[END OF SKILL]**/


    /**[START OF TECHNOLOGY]**/

    //Technology recycler view
    private fun setUpTechnologyRecyclerView() {

        try {

            technologyAdapter.submitList(project.technologyUsed)

            includeBinding.technologyRV.apply {

                setHasFixedSize(true)
                adapter = technologyAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            technologyAdapter.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTechnologyClicked(technology: Technology, position: Int) {

        showBottomSheetDialogForAddingTechnology(
                classForAddingProject,
                project,
                technology, position)
    }

    override fun onTechnologyDeleteBtnClicked(technology: Technology, position: Int) {

        deleteTechnology(technology, position)
        technologyAdapter.notifyItemRemoved(position)

        setUpTechnologyRecyclerView()
    }

    private fun deleteTechnology(technology: Technology, position: Int) {

        project.technologyUsed.remove(technology)

        Snackbar.make(binding.root, "Technology deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    project.technologyUsed.add(position, technology)
                    setUpTechnologyRecyclerView()
                }
                .show()
    }

    /**[END OF TECHNOLOGY]**/


    /**[START OF LINK RESOURCE]**/

    //Link resource recycler view
    private fun setUpLinkResourceRecyclerView() {

        try {

            project.resources.let {

                linkResourceAdapter.submitList(it?.urls)

                includeBinding.resourceLinkRV.apply {

                    setHasFixedSize(true)
                    adapter = linkResourceAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }

                linkResourceAdapter.setOnClickListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                project,
                link,
                position
        )
    }

    override fun onDeleteLinkClicked(link: Url, position: Int) {

        project.resources?.urls?.remove(link)
        setUpLinkResourceRecyclerView()

        Snackbar.make(binding.root, "Link deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    project.resources?.urls?.add(position, link)
                    setUpLinkResourceRecyclerView()
                }
                .show()

    }

    override fun onShareLinkBtnClicked(url: String) {

        Functions.shareAsText(
                url,
                "URL",
                requireContext()
        )
    }

    /**[END OF LINK RESOURCE]**/


    /**[START OF COLORS]**/

    //Colors recycler view
    private fun setUpColorsRecyclerView() {

        try {

            colorsAdapter.submitList(project.colors)

            includeBinding.colorsRV.apply {

                setHasFixedSize(true)
                adapter = colorsAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            colorsAdapter.setOnClickListener(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onColorClicked(color: Colors, position: Int) {

        showBottomSheetDialogForAddingColor(
                classForAddingProject,
                project,
                color,
                position
        )
    }

    /**[END OF COLORS]**/

    private var shouldSaveOnPause = true

    override fun onPause() {
        super.onPause()

        if (shouldSaveOnPause) {

            if (includeBinding.projectNameET.text.toString().trim().isNotEmpty()) {

                Log.d(TAG, "onPause: ")

                saveProjectToDatabase()
            }
        }
    }

    //option menu for saving the project to database
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_save_button, menu)

        menu.findItem(R.id.save_button).setOnMenuItemClickListener {

            if (includeBinding.projectNameET.text.toString().trim().isNotEmpty()) {

                saveProjectToDatabase()

                Snackbar.make(binding.root, "Project Saved", Snackbar.LENGTH_LONG)
                        .setAction("Go to home") {

                            requireActivity().onBackPressed()

                            shouldSaveOnPause = false
                        }
                        .show()

            } else {

                includeBinding.projectNameET.error = "Please give your project a name..."
            }
            true
        }
    }

    private fun saveProjectToDatabase() {

        Log.d(TAG, "saveProjectToDatabase: ")

        val projectName = includeBinding.projectNameET.text.toString().trim()
        val projectDescription = includeBinding.projectDescriptionET.editText?.text.toString().trim()

        val description = Description(projectName, projectDescription)

        val theme = Theme(
                includeBinding.themePrimaryColorTV.text.toString().trim(),
                includeBinding.themePrimaryColorDarkTV.text.toString().trim(),
                includeBinding.themeAccentColorTV.text.toString().trim(),
                includeBinding.themePrimaryTextColorTV.text.toString().trim(),
                includeBinding.themeSecondaryTextColorTV.text.toString().trim(),
                includeBinding.themeTextOnPrimaryColorTV.text.toString().trim()
        )

        project.description = description
        project.theme = theme
        project.modifiedOn = System.currentTimeMillis()

        if (isProjectReceivedForEditing) {

            projectViewModel.updateProject(project)
        } else {

            projectViewModel.insertProject(project)
        }

        Log.d(TAG, "saveProjectToDatabase: Project Saved : $project")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}