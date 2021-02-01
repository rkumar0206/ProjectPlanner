package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentBugBinding
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BugFragment : Fragment(R.layout.fragment_bug) {

    private val bugViewModel by viewModels<BugViewModel>()

    private var _binding: FragmentBugBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBugBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}