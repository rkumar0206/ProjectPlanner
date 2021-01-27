package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
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
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Feature
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.adapters.FeatureAdapter
import com.rohitthebest.projectplanner.ui.adapters.StringAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project),
        View.OnClickListener, FeatureAdapter.OnClickListener, StringAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var includeBinding: AddEditProjectLayoutBinding

    private lateinit var project: Project

    //adapters
    private lateinit var featureAdapter: FeatureAdapter
    private lateinit var skillAdapter: StringAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        project = Project()

        //init adapters
        featureAdapter = FeatureAdapter()
        skillAdapter = StringAdapter()

        setUpFeaturesRecyclerView()
        setUpSkillsRecyclerView()

        initListeners()
        setHasOptionsMenu(true)
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

                openFeatureBottomSheetDialog(position = if (project.features.size == 0) {
                    0
                } else {
                    project.features.lastIndex + 1
                })
            }
            includeBinding.addResourceBtn.id -> {

            }
            includeBinding.addSkillBtn.id -> {

                showDialogForAddingSkills(position = if (project.skillsRequired.size == 0) {
                    0
                } else {
                    project.skillsRequired.lastIndex + 1
                })
            }
            includeBinding.addTechnologyBtn.id -> {

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
    }

    private fun showDialogForAddingSkills(skill: String? = null, position: Int = 0) {

        MaterialDialog(requireContext()).show {

            title(text = "Add Skill")

            if (skill != null) {

                title(text = "Edit Skill")

                input(hint = "Edit Skill", prefill = skill,
                        inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) { _, charSequence ->

                    if (charSequence.toString().trim().isEmpty()) {

                        showToast(requireContext(), "Cannot edit empty skill!!!")
                    } else {

                        project.skillsRequired[position] = charSequence.toString().trim()
                        Log.d(TAG, "showDialogForAddingSkills: Skill edited at position $position Skill = ${charSequence.toString().trim()}")
                    }
                }
            } else {

                input(hint = "Add skill", inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) { _, charSequence ->

                    if (charSequence.toString().trim().isEmpty()) {

                        showToast(requireContext(), "Cannot add empty skill!!!")
                    } else {

                        project.skillsRequired.add(position, charSequence.toString().trim())

                        Log.d(TAG, "showDialogForAddingSkills: Skill Added at position $position Skill = ${charSequence.toString().trim()}")
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
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s?.isEmpty()!!) {

                        getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error = "This is a mandatory field!!"
                    } else {

                        getCustomView().findViewById<TextInputLayout>(R.id.featureNameET).error = null
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

                        project.features.add(position, featureToBeAdded)

                        showToast(requireContext(), "feature added")

                        Log.d(TAG, "openFeatureBottomSheetDialog: feature added at position $position " +
                                ": $featureToBeAdded")
                    } else {

                        project.features[position] = featureToBeAdded

                        Log.d(TAG, "openFeatureBottomSheetDialog: feature edited at position $position " +
                                ": $featureToBeAdded")
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