package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.Constants.SORT_BY_DATE_ASC
import com.rohitthebest.projectplanner.Constants.SORT_BY_DATE_DESC
import com.rohitthebest.projectplanner.Constants.SORT_BY_NAME_ASC
import com.rohitthebest.projectplanner.Constants.SORT_BY_NAME_DESC
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentTaskBinding
import com.rohitthebest.projectplanner.datastore.TaskMenuValues
import com.rohitthebest.projectplanner.datastore.TaskMenuValuesDataStore
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
import kotlinx.coroutines.*

private const val TAG = "TaskFragment"

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task), View.OnClickListener,
        TaskAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()
    private val taskViewModel by viewModels<TaskViewModel>()

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var project: Project
    private var projectKey: String = ""

    private lateinit var taskAdapter: TaskAdapter

    private var recyclerViewPosition = 1

    private lateinit var taskMenuDataStore: TaskMenuValuesDataStore

    private var sortingMethod = ""
    private var hideCompleted: Boolean = false

    private var isRefreshEnabled = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskBinding.bind(view)

        taskAdapter = TaskAdapter()

        taskMenuDataStore = TaskMenuValuesDataStore(requireContext())

        initListeners()

        getMessage()

        observeSortingMethodChange()

        textWatcher()

        setHasOptionsMenu(true)
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    TaskFragmentArgs.fromBundle(it)
                }

                projectKey = args?.message!!

                observeSortingMethodChange()

                getProjectFromDatabase(projectKey)

                //getTasksFromProjectKey()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeSortingMethodChange() {

        taskMenuDataStore.sortingFlow.observe(viewLifecycleOwner) {

            Log.d(TAG, "observeSortingMethodChange: ")

            sortingMethod = it.sortingMethod
            hideCompleted = it.hideCompletedTask

            if (isRefreshEnabled) {

                Log.d(TAG, "observeSortingMethodChange: isRefreshEnabled enters")

                getTasksFromProjectKey()

                //isRefreshEnabled = false
            }
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

    private fun getTasksFromProjectKey() {

        try {

            //added value 'isRefreshEnabled' because this viewModel function was called several times
            //when the elements inside this list was updated, causing the recyclerView to be setUp
            //many times
            taskViewModel.getAllTaskByProjectKey(projectKey)
                    .observe(viewLifecycleOwner) {

                        if (isRefreshEnabled) {

                            var filteredList = it

                            if (hideCompleted) {

                                filteredList = it.filter { t ->

                                    !t.isCompleted
                                }
                            }

                            Log.d(TAG, "getTasksFromProjectKey: $hideCompleted")
                            Log.d(TAG, "getTasksFromProjectKey: $sortingMethod")

                            if (filteredList.isNotEmpty()) {

                                binding.addFirstTaskBtn.hide()
                            } else {

                                binding.addFirstTaskBtn.showViewBySlidingAnimation()
                            }

                            when (sortingMethod) {

                                SORT_BY_DATE_ASC -> {

                                    setUpTaskRecyclerView(filteredList.sortedBy { t -> t.timeStamp })
                                }

                                SORT_BY_NAME_ASC -> {

                                    setUpTaskRecyclerView(filteredList.sortedBy { t -> t.taskName })
                                }

                                SORT_BY_NAME_DESC -> {

                                    setUpTaskRecyclerView(filteredList.sortedByDescending { t -> t.taskName })
                                }

                                else -> {

                                    setUpTaskRecyclerView(filteredList)
                                }
                            }

                            isRefreshEnabled = false

                            try {

                                Log.d(TAG, "getTasksFromProjectKey: receyclerViewPosition : $recyclerViewPosition")

                                binding.rvProjectTask.scrollToPosition(recyclerViewPosition)

                                recyclerViewPosition = 1

                            } catch (e: Exception) {

                                e.printStackTrace()
                            }
                        }

                    }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpTaskRecyclerView(taskList: List<Task>?) {

        try {

            Log.d(TAG, "setUpTaskRecyclerView: ")

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

        recyclerViewPosition = position

        task.isCompleted = !task.isCompleted

        taskViewModel.updateTask(task)

        isRefreshEnabled = true
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

                isRefreshEnabled = true
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

        isRefreshEnabled = true

        Snackbar.make(binding.root, "task deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {

                    recyclerViewPosition = position
                    taskViewModel.insertTask(task)

                    isRefreshEnabled = true
                }
                .show()
    }

    private fun getProjectFromDatabase(projectKey: String) {

        try {

            projectViewModel.getProjectByProjectKey(projectKey).observe(viewLifecycleOwner) {

                Log.d(TAG, "getProjectFromDatabase: ")

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        Log.d(TAG, "onCreateOptionsMenu: hideCompleted : $hideCompleted")

        inflater.inflate(R.menu.task_menu, menu)

        GlobalScope.launch {
            delay(200)

            withContext(Dispatchers.Main) {

                Log.d(TAG, "onCreateOptionsMenu: $hideCompleted")

                menu.findItem(R.id.menu_action_hieCompletedCB).isChecked = hideCompleted
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.menu_action_sort_by_date_created_ASC -> {

                saveTaskMenuValues(SORT_BY_DATE_ASC, hideCompleted)
                return true
            }

            R.id.menu_action_sort_by_name_ASC -> {

                saveTaskMenuValues(SORT_BY_NAME_ASC, hideCompleted)
                return true
            }

            R.id.menu_action_sort_by_date_created_DESC -> {

                saveTaskMenuValues(SORT_BY_DATE_DESC, hideCompleted)
                return true
            }

            R.id.menu_action_sort_by_name_DESC -> {

                saveTaskMenuValues(SORT_BY_NAME_DESC, hideCompleted)
                return true
            }

            R.id.menu_action_hieCompletedCB -> {

                item.isChecked = !item.isChecked
                saveTaskMenuValues(sortingMethod, item.isChecked)
                return true
            }
            R.id.menu_action_delete_all_completed -> {


                return true
            }

            else -> false

        }

    }

    private fun saveTaskMenuValues(sortBY: String, shouldHideComplete: Boolean) {

        Log.d(TAG, "saveTaskMenuValues: sortBy : $sortBY and hideCompleted = $shouldHideComplete")

        isRefreshEnabled = true
        GlobalScope.launch {

            taskMenuDataStore.storeSortingMethod(TaskMenuValues(sortBY, shouldHideComplete))
        }
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
                false
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
