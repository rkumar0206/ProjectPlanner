package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentBugBinding
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BugFragment"

@AndroidEntryPoint
class BugFragment : Fragment(R.layout.fragment_bug) {

    private val bugViewModel by viewModels<BugViewModel>()
    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentBugBinding? = null
    private val binding get() = _binding!!

    private var projectKey = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBugBinding.bind(view)

        getMessage()

        binding.addBugBtn.setOnClickListener {

            openBottomSheetForAddingBugReport()
        }
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    BugFragmentArgs.fromBundle(it)
                }

                projectKey = args?.projectMessage!!

                getBugsListFromProjectKey(projectKey)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getBugsListFromProjectKey(projectKey: String) {

        bugViewModel.getAllBugsByProjectKey(projectKey).observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                binding.noBugReportFoundTV.hide()
            } else {

                binding.noBugReportFoundTV.show()
            }

            setUpBugRecyclerView(it)
        }
    }

    private fun setUpBugRecyclerView(bugList: List<Bug>?) {

        try {

            //todo : set up the recycler view
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun openBottomSheetForAddingBugReport() {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add bug report")

            customView(
                    R.layout.add_bug_layout,
                    scrollable = true
            )

            val bugNameET = getCustomView().findViewById<TextInputLayout>(R.id.whatsBugET)
            val bugDescriptionET = getCustomView().findViewById<TextInputLayout>(R.id.bugDescriptionET)
            val possibleSolutionET = getCustomView().findViewById<TextInputLayout>(R.id.possibleSolutionET)

            positiveButton(text = "Save") {

                val bugName = bugNameET.editText?.text?.toString()?.trim()
                val bugDescription = bugDescriptionET.editText?.text?.toString()?.trim()
                val possibleSolution = possibleSolutionET.editText?.text?.toString()?.trim()

                if (bugName?.isNotEmpty()!! ||
                        bugDescription?.isNotEmpty()!!) {

                    saveBugReportToDatabase(
                            bugName,
                            bugDescription,
                            possibleSolution
                    )

                    dismiss()
                }
            }

            negativeButton(text = "Cancel") {

                it.dismiss()
            }
        }
    }

    private fun saveBugReportToDatabase(bugName: String, bugDescription: String?, possibleSolution: String?) {

        val bug = Bug(
                System.currentTimeMillis(),
                generateKey(),
                projectKey,
                bugName,
                bugDescription.toString(),
                possibleSolution,
                FALSE
        )

        bugViewModel.insert(bug)
    }

    private fun showBottomSheetForAddingLink() {

        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {

            title(text = "Add link")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}