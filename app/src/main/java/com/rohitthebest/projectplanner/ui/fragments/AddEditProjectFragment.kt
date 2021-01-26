package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

    }

    override fun onClick(v: View?) {


    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}