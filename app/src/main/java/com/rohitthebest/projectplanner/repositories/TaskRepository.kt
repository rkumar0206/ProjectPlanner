package com.rohitthebest.projectplanner.repositories

import com.rohitthebest.projectplanner.db.dao.TaskDao
import com.rohitthebest.projectplanner.db.entity.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(
        val dao: TaskDao
) {

    suspend fun insertTask(task: Task) = dao.insertTask(task)

    suspend fun insertTasks(tasks: List<Task>) = dao.insertTasks(tasks)

    suspend fun updateTask(task: Task) = dao.updateTask(task)

    suspend fun deleteTask(task: Task) = dao.deleteTask(task)

    suspend fun deleteAllTasks() = dao.deleteAllTasks()

    suspend fun deleteTaskByIsCompleted(isCompleted: Boolean) = dao.deleteTaskByIsCompleted(isCompleted)

    suspend fun deleteTaskByProjectKey(projectKey: String) = dao.deleteTaskByProjectKey(projectKey)

    suspend fun deleteTaskByProjectKeyAndIsCompleted(projectKey: String, isCompleted: Boolean) =
            dao.deleteTaskByProjectKeyAndIsCompleted(projectKey, isCompleted)

    fun getAllTasks() = dao.getAllTasks()

    fun getAllTaskByProjectKey(projectKey: String) = dao.getAllTaskByProjectKey(projectKey)

    fun getTaskCountByProjectKey(projectKey: String) = dao.getTaskCountByProjectKey(projectKey)
}