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
import android.widget.TextView
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
import com.rohitthebest.projectplanner.db.entity.Feature
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Technology
import com.rohitthebest.projectplanner.db.entity.Url
import com.rohitthebest.projectplanner.ui.adapters.FeatureAdapter
import com.rohitthebest.projectplanner.ui.adapters.LinkResourceAdapter
import com.rohitthebest.projectplanner.ui.adapters.StringAdapter
import com.rohitthebest.projectplanner.ui.adapters.TechnologyAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.isInternetAvailable
import com.rohitthebest.projectplanner.utils.Functions.Companion.openLinkInBrowser
import com.rohitthebest.projectplanner.utils.Functions.Companion.showNoInternetMessage
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import com.rohitthebest.projectplanner.utils.convertToHexString
import com.rohitthebest.projectplanner.utils.removeFocus
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint
import yuku.ambilwarna.AmbilWarnaDialog

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project),
    View.OnClickListener, FeatureAdapter.OnClickListener, StringAdapter.OnClickListener,
    TechnologyAdapter.OnClickListener, LinkResourceAdapter.OnClickListener {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        project = Project()

        //init adapters
        featureAdapter = FeatureAdapter()
        skillAdapter = StringAdapter()
        technologyAdapter = TechnologyAdapter()
        linkResourceAdapter = LinkResourceAdapter()

        setUpRecyclerViews()

        initListeners()
        setHasOptionsMenu(true)
    }

    //setting up all the recycler views
    private fun setUpRecyclerViews() {

        setUpFeaturesRecyclerView()
        setUpSkillsRecyclerView()
        setUpTechnologyRecyclerView()
        setUpLinkResourceRecyclerView()

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


    override fun onTechnologyClicked(technology: Technology, position: Int) {

        showBottomSheetDialogForAddingTechnology(technology, position)
    }

    override fun onTechnologyDeleteBtnClicked(technology: Technology, position: Int) {

        deleteTechnology(technology, position)
        technologyAdapter.notifyItemRemoved(position)

        setUpTechnologyRecyclerView()
    }

    //handling the clicks on feature
    override fun onFeatureClicked(feature: Feature, position: Int) {

        openFeatureBottomSheetDialog(feature, position)
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

    //option menu for saving the project to database
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_save_button, menu)

        menu.findItem(R.id.save_button).setOnMenuItemClickListener {

            //todo : save the project to database
            showToast(requireContext(), "save button clicked")
            true
        }
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
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addColorBtn.id -> {

            }
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

            includeBinding.themeSeeInLayoutBtn.id -> {

            }
            includeBinding.themeAccentColor.id -> {

            }
            includeBinding.themePrimaryColorBtn.id -> {

            }
            includeBinding.themePrimaryColorDarkBtn.id -> {

            }
            includeBinding.themePrimaryTextColor.id -> {

            }
            includeBinding.themeSecondaryTextColor.id -> {

            }
            includeBinding.themeTextOnPrimaryColorBtn.id -> {

            }

        }

        includeBinding.projectNameET.removeFocus()
        includeBinding.projectDescriptionET.editText?.removeFocus()
    }

    //showing bottomSheet for adding link resource to the list
    private fun showBottomSheetDialogForAddingLinkResource(url: Url? = null, position: Int) {

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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}