package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentTaskBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Task
import com.rohitthebest.projectplanner.ui.adapters.TaskAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.ui.viewModels.TaskViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.hideKeyBoard
import com.rohitthebest.projectplanner.utils.Functions.Companion.showKeyboard
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.hideViewBySlidingAnimation
import com.rohitthebest.projectplanner.utils.removeFocus
import com.rohitthebest.projectplanner.utils.showViewBySlidingAnimation
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TaskFragment"

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task), View.OnClickListener, TaskAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()
    private val taskViewModel by viewModels<TaskViewModel>()

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var project: Project

    private lateinit var taskAdapter: TaskAdapter

    private var recyclerViewPosition = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskBinding.bind(view)

        taskAdapter = TaskAdapter()

        initListeners()

        getMessage()

        textWatcher()
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    TaskFragmentArgs.fromBundle(it)
                }

                val projectKey = args?.message

                getProjectFromDatabase(projectKey!!)

                getTasksFromProjectKey(projectKey)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun textWatcher() {

        binding.etNewTask.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    binding.addTaskIB.hideViewBySlidingAnimation()
                    binding.addTaskTV.hideViewBySlidingAnimation()
                } else {

                    if (binding.addTaskIB.visibility != View.VISIBLE) {

                        binding.addTaskIB.showViewBySlidingAnimation()
                        binding.addTaskTV.showViewBySlidingAnimation()
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getTasksFromProjectKey(projectKey: String) {

        try {

            taskViewModel.getAllTaskByProjectKey(projectKey).observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {

                    binding.addFirstTaskBtn.hide()
                } else {

                    binding.addFirstTaskBtn.showViewBySlidingAnimation()
                }

                setUpTaskRecyclerView(it)

                try {
                    if (recyclerViewPosition !in 0..7) {

                        binding.rvProjectTask.scrollToPosition(recyclerViewPosition - 1)

                        recyclerViewPosition = 1
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpTaskRecyclerView(taskList: List<Task>?) {

        try {

            taskList?.let {

                taskAdapter.submitList(it)

                binding.rvProjectTask.apply {

                    setHasFixedSize(true)
                    adapter = taskAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }

                taskAdapter.setOnClickListener(this)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun onCheckChanged(task: Task, position: Int) {

        binding.etNewTask.editText?.removeFocus()

        recyclerViewPosition = position + 1

        task.isCompleted = if (task.isCompleted == FALSE) TRUE else FALSE

        taskViewModel.updateTask(task)
    }

    override fun onEditTaskClicked(task: Task, position: Int) {

        binding.etNewTask.editText?.removeFocus()

        recyclerViewPosition = position

        MaterialDialog(requireContext()).show {

            title(text = "Edit Task")

            input(
                    hint = "Task name",
                    prefill = task.taskName,
                    allowEmpty = false
            ) { _, charSequence ->

                task.taskName = charSequence.toString()
                taskViewModel.updateTask(task)

                dismiss()
            }

            positiveButton(text = "Save")
            negativeButton(text = "Cancel")
        }
    }

    override fun onDeleteTaskClicked(task: Task, position: Int) {

        binding.etNewTask.editText?.removeFocus()

        recyclerViewPosition = position

        taskViewModel.deleteTask(task)

        Snackbar.make(binding.root, "task deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    recyclerViewPosition = position
                    taskViewModel.insertTask(task)
                }
                .show()
    }

    private fun getProjectFromDatabase(projectKey: String) {

        try {

            projectViewModel.getProjectByProjectKey(projectKey).observe(viewLifecycleOwner) {

                if (it != null) {

                    project = it

                    Log.d(TAG, "getProjectFromDatabase: $project")

                    updateUi()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUi() {

        binding.tvProjectName.text = project.description.name
    }

    private fun initListeners() {

        binding.addFirstTaskBtn.setOnClickListener(this)
        binding.addTaskIB.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.addFirstTaskBtn.id -> {

                binding.etNewTask.editText?.showKeyboard(requireActivity())
            }

            binding.addTaskIB.id -> {

                if (binding.etNewTask.editText?.text.toString().trim().isNotEmpty()) {

                    addTaskToDatabase(binding.etNewTask.editText?.text.toString().trim())

                    binding.etNewTask.editText?.setText("")
                }
            }
        }
    }

    private fun addTaskToDatabase(taskName: String) {

        val task = Task(
                System.currentTimeMillis(),
                generateKey(),
                project.projectKey,
                taskName,
                "",
                FALSE
        )

        taskAdapter = TaskAdapter()

        taskViewModel.insertTask(task)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideKeyBoard(requireActivity())

        _binding = null
    }
}
