package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var includeBinding: AddEditProjectLayoutBinding

    private var project: Project? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        initListeners()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_save_button, menu)

        menu.findItem(R.id.save_button).setOnMenuItemClickListener {

            //todo : save the project to database
            showToast(requireContext(), "save button clicked")
            true
        }
    }

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


            }
            includeBinding.addResourceBtn.id -> {

            }
            includeBinding.addSkillBtn.id -> {

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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}