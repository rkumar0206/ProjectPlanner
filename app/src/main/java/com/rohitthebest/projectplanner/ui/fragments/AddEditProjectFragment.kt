package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding

class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project) {

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}