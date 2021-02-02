package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputLayout
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentBugBinding
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.ui.adapters.BugAdapter
import com.rohitthebest.projectplanner.ui.viewModels.BugViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BugFragment"

@AndroidEntryPoint
class BugFragment : Fragment(R.layout.fragment_bug), BugAdapter.OnClickListener {

    private val bugViewModel by viewModels<BugViewModel>()

    private var _binding: FragmentBugBinding? = null
    private val binding get() = _binding!!

    private var projectKey = ""

    private lateinit var bugAdapter: BugAdapter

    private var recyclerViewPosition = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBugBinding.bind(view)

        bugAdapter = BugAdapter()

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

            try {
                if (recyclerViewPosition !in 0..7) {

                    binding.bugRV.scrollToPosition(recyclerViewPosition - 1)

                    recyclerViewPosition = 1
                }
            } catch (e: Exception) {

                e.printStackTrace()
            }

        }
    }

    private fun setUpBugRecyclerView(bugList: List<Bug>?) {

        try {

            bugAdapter.submitList(bugList)

            binding.bugRV.apply {

                setHasFixedSize(true)
                adapter = bugAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            bugAdapter.setOnClickListener(this)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    override fun onBugDescriptionCBClicked(bug: Bug, position: Int) {

        recyclerViewPosition = position

        bug.isResolved = if (bug.isResolved == TRUE) FALSE else TRUE

        bugViewModel.updateBug(bug)
    }

    override fun onEditBugBtnClicked(bug: Bug, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onDeleteBugBtnClicked(bug: Bug, position: Int) {
        //TODO("Not yet implemented")
    }


    private fun openBottomSheetForAddingBugReport() {

        MaterialDialog(requireContext(), BottomSheet()).show {

            title(text = "Add bug report")

            customView(
                    R.layout.add_bug_layout,
                    scrollable = true
            )

            val bugDescriptionET = getCustomView().findViewById<TextInputLayout>(R.id.bugDescriptionET)
            val possibleSolutionET = getCustomView().findViewById<TextInputLayout>(R.id.possibleSolutionET)

            positiveButton(text = "Save") {
                val bugDescription = bugDescriptionET.editText?.text?.toString()?.trim()

                val possibleSolution = possibleSolutionET.editText?.text?.toString()?.trim()

                if (bugDescription?.isNotEmpty()!!) {

                    saveBugReportToDatabase(
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

    private fun saveBugReportToDatabase(bugDescription: String?, possibleSolution: String?) {

        val bug = Bug(
                System.currentTimeMillis(),
                generateKey(),
                projectKey,
                bugDescription.toString(),
                possibleSolution,
                FALSE
        )

        bugViewModel.insert(bug)

        Log.d(TAG, "saveBugReportToDatabase: BugReport inserted: $bug")

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}