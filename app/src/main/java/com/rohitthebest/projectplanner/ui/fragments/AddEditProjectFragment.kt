package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.Constants.TRUE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.SubTopic
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.ui.adapters.TopicAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.hideKeyBoard
import com.rohitthebest.projectplanner.utils.Functions.Companion.setDateInTextView
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener,
        TopicAdapter.OnClickListener {

    private val projectViewModel by viewModels<ProjectViewModel>()

    private var _binding: FragmentAddEditProjectBinding? = null
    private val binding get() = _binding!!
    private lateinit var includeBinding: AddEditProjectLayoutBinding

    private var currentTimeStamp = 0L

    private var project: Project? = null

    private lateinit var topicAdapter: TopicAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditProjectBinding.bind(view)

        includeBinding = binding.include

        topicAdapter = TopicAdapter()

        currentTimeStamp = System.currentTimeMillis()

        includeBinding.tvProjectDate.setDateInTextView(timeStamp = currentTimeStamp, startingText = "Date : ")

        initListeners()

        //observeChanges()
    }

    /*private fun observeChanges() {

        try {

            project?.let {

                projectViewModel.getProjectByProjectKey(it.projectKey).observe(viewLifecycleOwner) { p ->

                    if (project != null && p != null) {

                        Log.d(TAG, "observeChanges: Project found and calling setUpRecyclerView()")
                        Log.d(TAG, "\nobserveChanges: $p")

                        setUpRecyclerView(p.topics)
                    }
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }*/


    private fun initListeners() {

        includeBinding.addTopicBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addTopicBtn.id -> {

                if (includeBinding.etProjectName.text.toString().trim().isNotEmpty()) {

                    addEmptyProjectToDatabase()
                    hideAddBtnAndShowRV()
                } else {

                    includeBinding.etProjectName.requestFocus()
                    includeBinding.etProjectName.error = "You must give a name to your project."
                }
            }
        }

        hideKeyBoard(requireActivity())
    }

    private fun addEmptyProjectToDatabase() {

        val projectKey = generateKey()

        val topic = Topic(
                projectKey,
                "",
                FALSE,
                ArrayList(),
                ArrayList(),
                "",
                generateKey()
        )

        project = Project(
                currentTimeStamp,
                System.currentTimeMillis(),
                includeBinding.etProjectName.text.toString().trim(),
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
        }

        Log.d(TAG, "addEmptyProjectToDatabase: Empty Project Added")
    }

    private fun setUpRecyclerView(topics: java.util.ArrayList<Topic>) {

        try {

            topicAdapter.submitList(topics)

            includeBinding.rvProjectTopic.apply {

                setHasFixedSize(true)
                adapter = topicAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            topicAdapter.setOnClickListener(this)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /** [START OF TOPIC LISTENERS]**/

    override fun onItemClick(topic: Topic) {

        //todo : do on topic click
    }

    override fun addOnTopicButtonClicked(position: Int) {

        project?.let {

            it.topics.add(position + 1, Topic(it.projectKey, "", FALSE, ArrayList(), ArrayList(), "", generateKey()))

            //projectViewModel.updateProject(it)

            topicAdapter.notifyItemInserted(position + 1)

            Log.d(TAG, "addOnTopicButtonClicked: new empty topic is inserted in project ${it.projectKey}")
        }
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
                            //projectViewModel.deleteProject(it)
                            project = null
                            showAddBtnAndHideRV()
                            dialog.dismiss()
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

                            //projectViewModel.updateProject(it)

                        }
                        .show()
            }
        }
    }

    override fun onTopicCheckChanged(topic: Topic, position: Int, isChecked: Boolean) {

        project?.let {

            it.topics[position].isCompleted = if (isChecked) TRUE else FALSE

            topicAdapter.notifyItemChanged(position)

            //projectViewModel.updateProject(it)

            Log.d(TAG, "onTopicCheckChanged: topic check changed to $isChecked")

            //todo : calculate the progress here
        }
    }

    override fun onTopicNameChanged(topicName: String, position: Int, topic: Topic) {

        Log.d(TAG, "onTopicNameChanged: $topicName")
        project?.let {

            try {

                it.topics[position].topicName = topicName
                topicAdapter.notifyItemChanged(position)
            } catch (e: IndexOutOfBoundsException) {

                e.printStackTrace()
            }

            //projectViewModel.updateProject(it)

            Log.d(TAG, "onTopicNameChanged: topic name changed to $topicName")
        }
    }

    override fun onAddSubTopicClicked(topic: Topic, position: Int) {

        project?.let {

            val subTopic = SubTopic(
                    topic.topicKey,
                    "",
                    FALSE,
                    ArrayList(),
                    generateKey()
            )

            it.topics[position].subTopics = arrayListOf(subTopic)

            topicAdapter.notifyItemChanged(position)

        }
    }

    override fun onAddLinkBtnClicked(position: Int) {

        showToast(requireContext(), "onAddLinkBtnClicked $position")

        //todo : Add the link to the topic
    }

    override fun onAddMarkDownBtnClicked(position: Int) {

        showToast(requireContext(), "onAddMarkDownBtnClicked $position")

        //todo : Add the markdown to the topic
    }

    /** [END OF TOPIC LISTENERS]**/

    private fun showAddBtnAndHideRV() {

        try {

            includeBinding.addTopicBtn.show()
            includeBinding.rvProjectTopic.hide()
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun hideAddBtnAndShowRV() {

        try {

            includeBinding.addTopicBtn.hide()
            includeBinding.rvProjectTopic.show()
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun insertProjectToDatabase() {

        try {

            if (project != null) {

                Log.d(TAG, "insertProjectToDatabase: Project inserted")
                project?.let { projectViewModel.insertProject(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
}