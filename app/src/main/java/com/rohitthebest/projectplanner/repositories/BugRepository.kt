package com.rohitthebest.projectplanner.repositories

import com.rohitthebest.projectplanner.db.dao.BugDao
import com.rohitthebest.projectplanner.db.entity.Bug
import javax.inject.Inject

class BugRepository @Inject constructor(
        val dao: BugDao
) {

    suspend fun insertBug(bug: Bug) = dao.insertBug(bug)

    suspend fun insertBugs(bugs: List<Bug>) = dao.insertBugs(bugs)

    suspend fun updateBug(bug: Bug) = dao.updateBug(bug)

    suspend fun deleteBug(bug: Bug) = dao.deleteBug(bug)

    suspend fun deleteAllBug() = dao.deleteAllBug()

    suspend fun deleteByProjectKey(projectKey: String) = dao.deleteByProjectKey(projectKey)

    fun getAllBugList() = dao.getAllBugList()

    fun getAllBugsByProjectKey(projectKey: String) = dao.getAllBugsByProjectKey(projectKey)
}