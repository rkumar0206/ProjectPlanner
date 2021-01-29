package com.rohitthebest.projectplanner.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants.EDIT_TEXT_EMPTY_MESSAGE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.*
import com.rohitthebest.projectplanner.ui.adapters.*
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.*
import com.rohitthebest.projectplanner.utils.Functions.Companion.applyColor
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.openLinkInBrowser
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint
import yuku.ambilwarna.AmbilWarnaDialog

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

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

        project.modifiedOn = System.currentTimeMillis()
        project.projectKey = generateKey()
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
                        position = if (project.features.size == 0) {
                            0
                        } else {
                            project.features.lastIndex + 1
                        }
                )
            }
            includeBinding.addResourceBtn.id -> {

                showBottomSheetDialogForAddingLinkResource(
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
                        position = if (project.skillsRequired.size == 0) {
                            0
                        } else {
                            project.skillsRequired.lastIndex + 1
                        }
                )
            }
            includeBinding.addTechnologyBtn.id -> {

                showBottomSheetDialogForAddingTechnology(
                        position = if (project.technologyUsed.size == 0) {
                            0
                        } else {
                            project.technologyUsed.lastIndex + 1
                        }
                )
            }
            includeBinding.addColorBtn.id -> {

                showBottomSheetDialogForAddingColor(
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

    /**[START OF FEATURE]*/

    //setting up all the recycler views
    private fun setUpRecyclerViews() {

        setUpFeaturesRecyclerView()
        setUpSkillsRecyclerView()
        setUpTechnologyRecyclerView()
        setUpLinkResourceRecyclerView()
        setUpColorsRecyclerView()
    }

    //Showing bottom sheet dialog for adding/editing/deleting feature
    private fun openFeatureBottomSheetDialog(feature: Feature? = null, position: Int = 0) {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add New Feature")

            customView(
                    R.layout.add_feature_layout,
                    scrollable = true
            )

            if (feature != null) {

                getCustomView().findViewById<CardView>(R.id.deleteBtnOuter).show()

                title(text = "Edit Feature")

                getCustomView().findViewById<MaterialCardView>(R.id.deleteBtn).setOnClickListener {

                    project.features.remove(feature)
                    this.dismiss()

                    Snackbar.make(binding.root, "Feature deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {

                                project.features.add(position, feature)
                                setUpFeaturesRecyclerView()
                            }
                            .show()
                }

                setInitialFeatureValues(getCustomView(), feature)
            }

            val featureName = getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).editText
            val featureDescription = getCustomView().findViewById<TextInputLayout>(R.id.featureDescriptionET).editText
            val featureImplementation = getCustomView().findViewById<TextInputLayout>(R.id.featureInplementationET).editText

            //adding text watcher on feature name editText
            featureName?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error =
                                "This is a mandatory field!!"
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error =
                                null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            positiveButton(text = "Save") {

                if (featureName?.text.toString().trim().isEmpty()) {

                    showToast(requireContext(), "Cannot add empty feature!!")
                } else {

                    val featureToBeAdded = Feature(
                            featureName?.text.toString().trim(),
                            featureDescription?.text.toString().trim(),
                            featureImplementation?.text.toString().trim()
                    )

                    if (feature == null) {

                        //add

                        project.features.add(position, featureToBeAdded)

                        Log.d(
                                TAG,
                                "openFeatureBottomSheetDialog: feature added at position $position " +
                                        ": $featureToBeAdded"
                        )
                    } else {

                        //edit

                        project.features[position] = featureToBeAdded

                        Log.d(
                                TAG,
                                "openFeatureBottomSheetDialog: feature edited at position $position " +
                                        ": $featureToBeAdded"
                        )
                    }
                    it.dismiss()
                }
            }

        }.negativeButton(text = "Cancel") {

            it.dismiss()
        }.setOnDismissListener {

            setUpFeaturesRecyclerView()
        }
    }

    private fun setInitialFeatureValues(customView: View, feature: Feature) {

        val featureName = customView.findViewById<TextInputLayout>(R.id.featureNameET).editText
        val featureDescription = customView.findViewById<TextInputLayout>(R.id.featureDescriptionET).editText
        val featureImplementation = customView.findViewById<TextInputLayout>(R.id.featureInplementationET).editText

        featureName?.setText(feature.name)
        featureDescription?.setText(feature.description)
        featureImplementation?.setText(feature.implementation)

    }

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

        openFeatureBottomSheetDialog(feature, position)
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

        showDialogForAddingSkills(text, position)
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

    private fun showDialogForAddingSkills(skill: String? = null, position: Int = 0) {

        MaterialDialog(requireContext()).show {

            title(text = "Add Skill")

            if (skill != null) {

                title(text = "Edit Skill")

                input(
                        hint = "Edit Skill", prefill = skill,
                        inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                ) { _, charSequence ->

                    if (charSequence.toString().trim().isEmpty()) {

                        showToast(requireContext(), "Cannot edit empty skill!!!")
                    } else {

                        project.skillsRequired[position] = charSequence.toString().trim()
                        Log.d(
                                TAG,
                                "showDialogForAddingSkills: Skill edited at position $position Skill = ${
                                    charSequence.toString().trim()
                                }"
                        )
                    }
                }
            } else {

                input(hint = "Add skill", inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) { _, charSequence ->

                    if (charSequence.toString().trim().isEmpty()) {

                        showToast(requireContext(), "Cannot add empty skill!!!")
                    } else {

                        project.skillsRequired.add(position, charSequence.toString().trim())

                        Log.d(
                                TAG,
                                "showDialogForAddingSkills: Skill Added at position $position Skill = ${
                                    charSequence.toString().trim()
                                }"
                        )
                    }
                }
            }
        }.negativeButton(text = "Cancel") {

            it.dismiss()
        }.setOnDismissListener {

            setUpSkillsRecyclerView()
        }
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

        showBottomSheetDialogForAddingTechnology(technology, position)
    }

    override fun onTechnologyDeleteBtnClicked(technology: Technology, position: Int) {

        deleteTechnology(technology, position)
        technologyAdapter.notifyItemRemoved(position)

        setUpTechnologyRecyclerView()
    }

    //showing bottomSheet for adding technology to the list
    private fun showBottomSheetDialogForAddingTechnology(
            technology: Technology? = null,
            position: Int = 0
    ) {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add technology used")

            customView(
                    R.layout.add_technology_layout,
                    scrollable = true
            )

            val techName = getCustomView().findViewById<TextInputLayout>(R.id.techNameET).editText
            val techTextColorBtn = getCustomView().findViewById<Button>(R.id.techTextColorBtn)
            val techBackgroundColorBtn =
                    getCustomView().findViewById<Button>(R.id.techBackgroundColorBtn)
            val previewText = getCustomView().findViewById<TextView>(R.id.previewTechTV)
            val previewBackgroundColor = getCustomView().findViewById<CardView>(R.id.previewTechCV)

            if (technology != null) {

                title(text = "Edit")

                getCustomView().findViewById<CardView>(R.id.deleteTechnologyBtnOuter).show()

                getCustomView().findViewById<MaterialCardView>(R.id.deleteTechnologyBtn)
                        .setOnClickListener {

                            deleteTechnology(technology, position)
                            this.dismiss()
                        }

                initializeTechnologyField(getCustomView(), technology)
            }

            techName?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        getCustomView().findViewById<TextInputLayout>(R.id.techNameET).error =
                                "Mandatory field!!"
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.techNameET).error = null
                    }

                    previewText.text = s.toString().trim()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            var mDefaultTextColor: Int = if (technology != null) {

                if (technology.textColor == null) {

                    Color.WHITE
                } else {

                    technology.textColor!!
                }

            } else {

                Color.WHITE
            }
            var mDefaultBackgroundColor = if (technology != null) {
                technology.backgroundColor ?: Color.parseColor("#FF6E40")

            } else {

                Color.parseColor("#FF6E40")
            }

            techTextColorBtn.setOnClickListener {

                AmbilWarnaDialog(requireContext(),
                        mDefaultTextColor,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {}

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                                mDefaultTextColor = color

                                showToast(requireContext(), mDefaultTextColor.convertToHexString())

                                previewText.setTextColor(mDefaultTextColor)
                                techTextColorBtn.setBackgroundColor(mDefaultTextColor)
                            }
                        }
                ).show()

            }
            techBackgroundColorBtn.setOnClickListener {

                AmbilWarnaDialog(requireContext(),
                        mDefaultBackgroundColor,
                        object : AmbilWarnaDialog.OnAmbilWarnaListener {
                            override fun onCancel(dialog: AmbilWarnaDialog?) {}

                            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                                mDefaultBackgroundColor = color
                                showToast(requireContext(), color.toString())

                                previewBackgroundColor.setCardBackgroundColor(mDefaultBackgroundColor)
                                techBackgroundColorBtn.setBackgroundColor(mDefaultBackgroundColor)
                            }
                        }
                ).show()
            }

            positiveButton(text = "Save") {

                if (techName?.text.toString().trim().isEmpty()) {

                    showToast(requireContext(), "Cannot add empty technology...")
                } else {

                    val technologyForAdding = Technology(
                            techName?.text.toString().trim(),
                            mDefaultBackgroundColor,
                            mDefaultTextColor
                    )

                    if (technology == null) {

                        //add
                        project.technologyUsed.add(position, technologyForAdding)

                        Log.d(
                                TAG,
                                "showBottomSheetDialogForAddingTechnology: technology Added at position $position : $technologyForAdding"
                        )

                    } else {

                        //edit
                        project.technologyUsed[position] = technologyForAdding

                        Log.d(
                                TAG,
                                "showBottomSheetDialogForAddingTechnology: technology edited at position $position"
                        )
                    }
                }
            }

        }.negativeButton(text = "Cancel") {

            it.dismiss()
        }.setOnDismissListener {

            setUpTechnologyRecyclerView()
        }
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

    private fun initializeTechnologyField(customView: View, technology: Technology) {

        val techName = customView.findViewById<TextInputLayout>(R.id.techNameET).editText
        val techTextColorBtn = customView.findViewById<Button>(R.id.techTextColorBtn)
        val techBackgroundColorBtn = customView.findViewById<Button>(R.id.techBackgroundColorBtn)
        val previewTextColor = customView.findViewById<TextView>(R.id.previewTechTV)
        val previewBackgroundColor = customView.findViewById<CardView>(R.id.previewTechCV)

        previewTextColor.text = technology.name
        previewTextColor.setTextColor(technology.textColor!!)
        previewBackgroundColor.setCardBackgroundColor(technology.backgroundColor!!)

        techName?.setText(technology.name)
        techTextColorBtn.setBackgroundColor(technology.textColor!!)
        techBackgroundColorBtn.setBackgroundColor(technology.backgroundColor)
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

    //showing bottomSheet for adding link resource to the list
    private fun showBottomSheetDialogForAddingLinkResource(url: Url? = null, position: Int = 0) {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add Link/Url")

            customView(
                    R.layout.add_link_resource_dialog_layout,
                    scrollable = true
            )

            val linkET = getCustomView().findViewById<TextInputLayout>(R.id.linkET).editText
            val linkNameET = getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).editText

            if (url != null) {

                initializeUrlFields(getCustomView(), url)
            }

            linkET?.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        getCustomView().findViewById<TextInputLayout>(R.id.linkET).error =
                                EDIT_TEXT_EMPTY_MESSAGE
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.linkET).error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            linkNameET?.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).error =
                                EDIT_TEXT_EMPTY_MESSAGE
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.linkNameET).error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            positiveButton(text = "Save") {

                if (
                        linkET?.text.toString().trim().isEmpty() ||
                        linkNameET?.text.toString().trim().isEmpty()
                ) {

                    showToast(requireContext(), "Cannot add empty url!!!")
                } else {

                    val urlToBeAdded = Url(
                            linkNameET?.text.toString().trim(),
                            linkET?.text.toString().trim()
                    )

                    if (url == null) {

                        //add

                        project.resources?.urls?.add(position, urlToBeAdded)

                        Log.d(
                                TAG,
                                "showBottomSheetDialogForAddingLinkResource: url added at position : $position"
                        )
                    } else {

                        //edit

                        project.resources?.urls?.set(position, urlToBeAdded)

                        Log.d(
                                TAG,
                                "showBottomSheetDialogForAddingLinkResource: url edited at position : $position"
                        )
                    }

                }
            }

        }.setOnDismissListener {

            setUpLinkResourceRecyclerView()
        }
    }

    private fun initializeUrlFields(customView: View, url: Url) {

        val linkET = customView.findViewById<TextInputLayout>(R.id.linkET).editText
        val linkNameET = customView.findViewById<TextInputLayout>(R.id.linkNameET).editText

        linkET?.setText(url.url)
        linkNameET?.setText(url.urlName)
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
                color,
                position
        )
    }

    //showing bottomSheet for adding colors to the list
    private fun showBottomSheetDialogForAddingColor(color: Colors? = null, position: Int = 0) {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add colors")

            customView(
                    R.layout.add_color_layout,
                    scrollable = true
            )

            val colorBtn = getCustomView().findViewById<Button>(R.id.chooseColorBtn)
            val colorHexEt = getCustomView().findViewById<EditText>(R.id.colorHexET)
            val colorNameET = getCustomView().findViewById<TextInputLayout>(R.id.colorNameEt).editText

            if (color != null) {

                //for editing

                getCustomView().findViewById<CardView>(R.id.deleteColorBtnOuter).show()
                getCustomView().findViewById<MaterialCardView>(R.id.deleteColorBtn).setOnClickListener {

                    project.colors.remove(color)
                    this.dismiss()

                    Snackbar.make(binding.root, "Color deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {

                                project.colors.add(position, color)
                                setUpColorsRecyclerView()
                            }
                            .show()
                }

                initializeColorFields(getCustomView(), color)
            }

            colorBtn.setOnClickListener {

                val hexCode = if (colorHexEt.text.toString().trim().startsWith("#")) {

                    colorHexEt.text.toString().trim()
                } else {
                    "#${colorHexEt.text.toString().trim()}"
                }

                colorBtn.openColorPicker(
                        requireContext(),

                        if (colorHexEt.text.toString().trim().isEmpty()) {

                            Color.parseColor(getString(R.string._119A17))
                        } else {

                            if (hexCode.isValidHexCode()) {

                                Color.parseColor(hexCode)
                            } else {

                                Color.parseColor(getString(R.string._119A17))
                            }
                        },
                        editText = colorHexEt
                )
            }

            setTextWatcherInColorEditTexts(colorBtn, colorHexEt, getCustomView().findViewById(R.id.colorNameEt))

            positiveButton(text = "Save") {

                if (
                        colorHexEt.text.toString().trim().isEmpty() || colorNameET?.text.toString().trim().isEmpty()
                ) {

                    showToast(requireContext(), "Mandatory fields can't be empty!!!", Toast.LENGTH_LONG)
                } else {

                    val hexCode = if (colorHexEt.text.toString().trim().startsWith("#")) {

                        colorHexEt.text.toString().trim()
                    } else {
                        "#${colorHexEt.text.toString().trim()}"
                    }

                    if (hexCode.isValidHexCode()) {

                        val colorToBeAdded = Colors(
                                colorNameET?.text.toString().trim(),
                                hexCode
                        )

                        if (color != null) {

                            //edit

                            project.colors[position] = colorToBeAdded

                            Log.d(TAG, "showBottomSheetDialogForAddingColor: color edited at position $position Color : $colorToBeAdded")

                        } else {

                            //add

                            project.colors.add(position, colorToBeAdded)

                            Log.d(TAG, "showBottomSheetDialogForAddingColor: color added at position $position Color : $colorToBeAdded")

                        }
                    } else {

                        showToast(requireContext(), "Incorrect hex code")
                    }
                }
            }
        }.negativeButton(text = "Cancel") {

            it.dismiss()
        }.setOnDismissListener {

            setUpColorsRecyclerView()
        }
    }

    private fun setTextWatcherInColorEditTexts(viewToColored: View, colorHexEt: EditText?, coloNameEt: TextInputLayout?) {

        colorHexEt?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    colorHexEt.error = EDIT_TEXT_EMPTY_MESSAGE
                } else {

                    colorHexEt.error = null

                    Log.d(TAG, "onTextChanged: s.length = ${s.length}")

                    if (s.length in 6..7) {

                        Log.d(TAG, "onTextChanged: s.length is in range 6..7")

                        val hexCode = if (s.toString().trim().startsWith("#")) {
                            s.toString().trim()
                        } else {
                            "#${s.toString().trim()}"
                        }

                        if (hexCode.isValidHexCode()) {

                            applyColor(
                                    requireContext(),
                                    viewToColored,
                                    hexCode = hexCode
                            )
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        coloNameEt?.editText?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    coloNameEt.error = EDIT_TEXT_EMPTY_MESSAGE
                } else {

                    coloNameEt.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun initializeColorFields(customView: View, color: Colors) {

        val colorBtn = customView.findViewById<Button>(R.id.chooseColorBtn)
        val colorHexEt = customView.findViewById<EditText>(R.id.colorHexET)
        val colorNameET = customView.findViewById<TextInputLayout>(R.id.colorNameEt).editText

        colorBtn.setBackgroundColor(Color.parseColor(color.colorHexCode))
        colorHexEt.setText(color.colorHexCode)
        colorNameET?.setText(color.colorName)
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

        projectViewModel.insertProject(project)

        Log.d(TAG, "saveProjectToDatabase: Project Saved : $project")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}