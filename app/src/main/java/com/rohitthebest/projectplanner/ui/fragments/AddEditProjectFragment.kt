package com.rohitthebest.projectplanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AddEditProjectLayoutBinding
import com.rohitthebest.projectplanner.databinding.FragmentAddEditProjectBinding
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.ui.adapters.TopicAdapter
import com.rohitthebest.projectplanner.ui.viewModels.ProjectViewModel
import com.rohitthebest.projectplanner.utils.Functions.Companion.generateKey
import com.rohitthebest.projectplanner.utils.Functions.Companion.hide
import com.rohitthebest.projectplanner.utils.Functions.Companion.setDateInTextView
import com.rohitthebest.projectplanner.utils.Functions.Companion.show
import com.rohitthebest.projectplanner.utils.Functions.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditProjectFragment"

@AndroidEntryPoint
class AddEditProjectFragment : Fragment(R.layout.fragment_add_edit_project), View.OnClickListener, TopicAdapter.OnClickListener {

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

        observeChanges()
    }

    private fun observeChanges() {

        try {
            project?.let {

                projectViewModel.getProjectByProjectKey(project!!.projectKey).observe(viewLifecycleOwner) { project ->

                    if (project != null) {

                        Log.i(TAG, "observeChanges: ${it.topics}")
                        setUpTopicRecyclerView(it.topics)
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpTopicRecyclerView(topics: List<Topic>) {

        try {

            if (topics.isNotEmpty()) {

                topicAdapter.submitList(topics)

                includeBinding.rvProjectTopic.apply {

                    adapter = topicAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                }

                topicAdapter.setOnClickListener(this)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(topic: Topic) {

        //todo : handle topic click
    }

    override fun addOnTopicButtonClicked() {

        project?.let {

            val topic = Topic(
                    it.projectKey,
                    "",
                    FALSE,
                    ArrayList(),
                    ArrayList(),
                    "",
                    generateKey()
            )

            it.topics.add(topic)

            projectViewModel.updateProject(it)

            //topicAdapter.notifyItemInserted(topicAdapter.itemCount)

            observeChanges()

            showToast(requireContext(), "added")
        }
    }

    override fun onClearTopicButtonClicked(topic: Topic, position: Int) {

        project?.let {

            it.topics.remove(topic)

            projectViewModel.updateProject(it)

            Log.i(TAG, "onClearTopicButtonClicked: ${topicAdapter.itemCount}")

            //if the item is the first item and also it is the only item then deleting the whole project
            if (position == 0 && topicAdapter.itemCount < 1) {

                projectViewModel.deleteProject(it)
                project = null
                showAddBtnAndHideRV()
            }
            topicAdapter.notifyItemRemoved(position)

            showToast(requireContext(), "Topic deleted")
        }
    }


    private fun initListeners() {

        includeBinding.addTopicBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.addTopicBtn.id -> {

                if (includeBinding.etProjectName.text.toString().trim().isNotEmpty()) {

                    addProjectToDatabase()
                    hideAddBtnAndShowRV()
                } else {

                    includeBinding.etProjectName.requestFocus()
                    includeBinding.etProjectName.error = "You must give a name to your project."
                }
            }
        }
    }

    private fun addProjectToDatabase() {

        Log.i(TAG, "addProjectToDatabase: ")

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

                modifiedOn = System.currentTimeMillis(),
                projectName = includeBinding.etProjectName.text.toString().trim(),
                projectProgress = 0,
                topics = arrayListOf(topic),
                urls = ArrayList(),
                projectKey = projectKey,
                markDown = ""
        )

/*
        project?.apply {

            modifiedOn = System.currentTimeMillis()
            projectName = includeBinding.etProjectName.text.toString().trim()
            projectProgress = 0
            topics = arrayListOf(topic)
            urls = ArrayList()
            this.projectKey = projectKey
            markDown = ""
        }
*/

        projectViewModel.insertProject(project!!)

        observeChanges()

        showToast(requireContext(), "Project Added")
    }


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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}