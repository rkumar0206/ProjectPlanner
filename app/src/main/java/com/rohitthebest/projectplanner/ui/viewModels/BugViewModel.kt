package com.rohitthebest.projectplanner.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.repositories.BugRepository
import kotlinx.coroutines.launch

class BugViewModel @ViewModelInject constructor(
        val repository: BugRepository
) : ViewModel() {

    fun insert(bug: Bug) = viewModelScope.launch {

        repository.insertBug(bug)
    }

    fun insertBugs(bugs: List<Bug>) = viewModelScope.launch {

        repository.insertBugs(bugs)
    }

    fun updateBug(bug: Bug) = viewModelScope.launch {

        repository.updateBug(bug)
    }

    fun deleteBug(bug: Bug) = viewModelScope.launch {
        repository.deleteBug(bug)
    }

    fun deleteAllBug() = viewModelScope.launch {
        repository.deleteAllBug()
    }

    fun deleteByProjectKey(projectKey: String) = viewModelScope.launch {

        repository.deleteByProjectKey(projectKey)
    }

    fun getAllBugList() = repository.getAllBugList().asLiveData()

    fun getAllBugsByProjectKey(projectKey: String) = repository.getAllBugsByProjectKey(projectKey).asLiveData()
}