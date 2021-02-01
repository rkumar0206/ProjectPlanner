package com.rohitthebest.projectplanner.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.repositories.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel @ViewModelInject constructor(
        val repository: ProjectRepository
) : ViewModel() {

    fun insertProject(project: Project) = viewModelScope.launch {

        repository.insertProject(project)
    }

    fun insertProjectList(projects: List<Project>) = viewModelScope.launch {

        repository.insertProjects(projects)
    }

    fun updateProject(project: Project) = viewModelScope.launch {

        repository.updateProject(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch {

        repository.deleteProject(project)
    }

    fun deleteAllProjects() = viewModelScope.launch {

        repository.deleteAllProjects()
    }

    fun getProjectByProjectKey(projectKey: String) = repository.getProjectByProjectKey(projectKey)

    val projects = repository.getAllProjects().asLiveData()

    fun getResourceList() = repository.getResourceList()

    fun getSkillsLists() = repository.getSkillsLists()

    fun getTechnologyList() = repository.getTechnologyList()
}