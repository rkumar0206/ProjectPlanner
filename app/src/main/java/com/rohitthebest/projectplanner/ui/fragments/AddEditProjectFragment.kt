package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener {

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!
    private lateinit var includeBinding: AddEditProjectLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        initListeners()
    }

    private fun initListeners() {

        includeBinding.addTopicBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addTopicBtn.id -> {


            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}