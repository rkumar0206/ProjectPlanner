package com.rohitthebest.projectplanner.repositories

import com.rohitthebest.projectplanner.db.dao.ProjectDao
import com.rohitthebest.projectplanner.db.entity.Project
import javax.inject.Inject

class ProjectRepository @Inject constructor(
        var dao: ProjectDao
) {

    suspend fun insertProject(project: Project) = dao.insertProject(project)

    suspend fun insertProjects(projects: List<Project>) = dao.insertProjects(projects)

    suspend fun deleteProject(project: Project) = dao.deleteProject(project)

    suspend fun deleteAllProjects() = dao.deleteAllProjects()

    suspend fun updateProject(project: Project) = dao.updateProject(project)

    fun getAllProjects() = dao.getAllProjects()

    fun getProjectByProjectKey(projectKey: String) = dao.getProjectByProjectKey(projectKey)
}