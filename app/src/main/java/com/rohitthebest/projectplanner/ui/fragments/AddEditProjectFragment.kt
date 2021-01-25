package com.rohitthebest.projectplanner.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.ui.adapters.TopicAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.hideKeyBoard
import com.rohitthebest.projectplanner.utils.Functions.Companion.invisible
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.showKeyboard
import com.rohitthebest.projectplanner.utils.converters.GsonConverter
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener,
        TopicAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!

    private var currentTimeStamp = 0L

    private var project: Project? = null

    private lateinit var topicAdapter: TopicAdapter

    private var progress: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        topicAdapter = TopicAdapter()

        currentTimeStamp = System.currentTimeMillis()

        initListeners()

        binding.pbProject.progress = 0

        getMessage()

        textWatcher()

    }

    private fun textWatcher() {

        binding.etAddNewTopic.editText?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    if (binding.addTopicIB.visibility == View.VISIBLE) {

                        binding.addTopicIB.hideButton()
                        binding.addTopicTV.hideButton()
                    }
                } else {

                    if (binding.addTopicIB.visibility != View.VISIBLE) {

                        binding.addTopicIB.showButton()
                        binding.addTopicTV.showButton()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getMessage() {

        try {

            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    AddEditProjectFragmentArgs.fromBundle(it)
                }

                project = args?.projectMessage?.let { GsonConverter().convertJsonStringToProject(it) }

                project?.let {

                    binding.etProjectName.setText(it.projectName)

                    setUpRecyclerView(it.topics)

                    calculateProgress(it)
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateProgress(project: Project) {

        if (project.topics.size != 0) {

            val completedTopics = project.topics.filter {

                it.isCompleted == TRUE
            }

            progress = (completedTopics.size * 100) / project.topics.size

            binding.pbProject.progress = progress
            binding.tvProgressProject.text = "$progress%"
        }
    }

    private fun initListeners() {

        binding.addTopicIB.setOnClickListener(this)
        binding.addFirstTopicBtn.setOnClickListener(this)
    }

    private fun addEmptyProject(topicName: String) {

        val projectKey = generateKey()

        val topic = Topic(
                projectKey,
                topicName,
                FALSE,
                ArrayList(),
                ArrayList(),
                "",
                generateKey()
        )

        project = Project(
                currentTimeStamp,
                System.currentTimeMillis(),
                binding.etProjectName.text.toString().trim(),
                0,
                arrayListOf(topic),
                ArrayList(),
                projectKey,
                ""
        )

        project?.let {

            //projectViewModel.insertProject(it)
            //observeChanges()

            setUpRecyclerView(it.topics)

            binding.addFirstTopicBtn.hideButton()
        }

        Log.d(TAG, "addEmptyProjectToDatabase: Empty Project Added")
    }

    private fun setUpRecyclerView(topics: java.util.ArrayList<Topic>) {

        try {

            topicAdapter.submitList(topics)

            binding.addFirstTopicBtn.hideButton()

            binding.rvProjectTopic.apply {

                setHasFixedSize(true)
                adapter = topicAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            topicAdapter.setOnClickListener(this)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun addTopic(position: Int, topicName: String) {

        project?.let {

            it.topics.add(position + 1, Topic(it.projectKey, topicName, FALSE, ArrayList(), ArrayList(), "", generateKey()))

            //projectViewModel.updateProject(it)

            topicAdapter.notifyItemInserted(position + 1)

            calculateProgress(it)

            updateModifiedOnValue()

            Log.d(TAG, "addOnTopicButtonClicked: new empty topic is inserted in project ${it.projectKey}")
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.addTopicIB.id -> {

                if (project == null) {

                    addEmptyProject(binding.etAddNewTopic.editText?.text.toString().trim())

                    binding.etAddNewTopic.editText?.setText("")
                } else {

                    project?.let {

                        addTopic(it.topics.lastIndex, binding.etAddNewTopic.editText?.text.toString().trim())
                        binding.etAddNewTopic.editText?.setText("")
                    }
                }
            }

            binding.addFirstTopicBtn.id -> {

                binding.etAddNewTopic.requestFocus()
                binding.etAddNewTopic.editText?.let { showKeyboard(requireActivity(), it) }
            }
        }
    }

    /** [START OF TOPIC LISTENERS]**/

    override fun onItemClick(topic: Topic) {

        //todo : do on topic click
    }

    override fun onClearTopicButtonClicked(topic: Topic, position: Int) {

        project?.let {

            Log.d(TAG, "onClearTopicButtonClicked: $position")

            //if the item is the first item and also it is the only item then deleting the whole project
            if (position == 0 && topicAdapter.itemCount <= 1) {

                MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Are you sure?")
                        .setMessage("This with delete the only topic in your project.")
                        .setPositiveButton("Delete") { dialog, _ ->

                            it.topics.remove(topic)
                            topicAdapter.notifyItemRemoved(position)
                            projectViewModel.deleteProject(it)
                            project = null
                            dialog.dismiss()

                            binding.addFirstTopicBtn.showButton()
                            progress = 0
                            binding.pbProject.progress = progress
                            binding.tvProgressProject.text = "0%"
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->

                            dialog.dismiss()
                        }
                        .create()
                        .show()

            } else {

                it.topics.remove(topic)

                topicAdapter.notifyItemRemoved(position)

                //projectViewModel.updateProject(it)

                Log.d(TAG, "onClearTopicButtonClicked: ${topicAdapter.itemCount}")

                Snackbar.make(binding.root, "Topic Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") { _ ->

                            it.topics.add(position, topic)

                            topicAdapter.notifyItemInserted(position)

                            calculateProgress(it)
                            //projectViewModel.updateProject(it)

                        }
                        .show()

                calculateProgress(it)
            }

            updateModifiedOnValue()
        }
    }

    override fun onTopicCheckChanged(topic: Topic, position: Int, isChecked: Boolean) {

        project?.let {

            Log.d(TAG, "onTopicCheckChanged: ")

            it.topics[position].isCompleted = if (isChecked) TRUE else FALSE

            updateModifiedOnValue()

            topicAdapter.notifyItemChanged(position)

            //projectViewModel.updateProject(it)

            Log.d(TAG, "onTopicCheckChanged: topic check changed to $isChecked")

            calculateProgress(it)
        }
    }

    /** [END OF TOPIC LISTENERS]**/

    private fun updateModifiedOnValue() {

        project?.let {

            it.modifiedOn = System.currentTimeMillis()

            Log.d(TAG, "updateModifiedOnValue: ${it.modifiedOn}")

        }
    }

    private fun insertProjectToDatabase() {

        try {

            if (project != null) {

                project?.let {

                    it.projectName = binding.etProjectName.text.toString().trim()
                    it.projectProgress = progress

                    projectViewModel.insertProject(it)
                    Log.d(TAG, "insertProjectToDatabase: Project inserted")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun View.showButton() {

        this.show()

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.add_topic_button_visible_anim).apply {

            duration = 600L
            interpolator = FastOutSlowInInterpolator()
            this.startOffset = 0
        }
        startAnimation(animation)
    }

    private fun View.hideButton() {

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.add_topic_button_invisible_anim).apply {

            duration = 600L
            interpolator = FastOutSlowInInterpolator()
            this.startOffset = 0
        }

        startAnimation(animation)

        this.invisible()

        if (this.id == binding.addFirstTopicBtn.id) {

            binding.addFirstTopicBtn.hide()
        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: ")

        try {

            insertProjectToDatabase()
            hideKeyBoard(requireActivity())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}