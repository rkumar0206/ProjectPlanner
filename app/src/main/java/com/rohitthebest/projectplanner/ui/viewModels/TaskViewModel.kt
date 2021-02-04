package com.rohitthebest.projectplanner.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.projectplanner.db.entity.Task
import com.rohitthebest.projectplanner.repositories.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
        val repository: TaskRepository
) : ViewModel() {

    fun insertTask(task: Task) = viewModelScope.launch {

        repository.insertTask(task)
    }

    fun insertTasks(tasks: List<Task>) = viewModelScope.launch {
        repository.insertTasks(tasks)
    }

    fun updateTask(task: Task) = viewModelScope.launch {

        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {

        repository.deleteTask(task)
    }

    fun deleteAllTasks() = viewModelScope.launch {

        repository.deleteAllTasks()
    }

    fun deleteTaskByIsCompleted(isCompleted: Boolean) = viewModelScope.launch {

        repository.deleteTaskByIsCompleted(isCompleted)
    }

    fun deleteTaskByProjectKey(projectKey: String) = viewModelScope.launch {

        repository.deleteTaskByProjectKey(projectKey)
    }

    fun deleteTaskByProjectKeyAndIsCompleted(projectKey: String, isCompleted: Boolean) =
            viewModelScope.launch {

                repository.deleteTaskByProjectKeyAndIsCompleted(projectKey, isCompleted)
            }

    var allTasks = repository.getAllTasks().asLiveData()

    fun getAllTaskByProjectKey(projectKey: String) =
            repository.getAllTaskByProjectKey(projectKey).asLiveData()

    fun getTaskCountByProjectKey(projectKey: String) =
            repository.getTaskCountByProjectKey(projectKey)
}