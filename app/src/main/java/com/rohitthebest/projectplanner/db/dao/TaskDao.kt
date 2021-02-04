package com.rohitthebest.projectplanner.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rohitthebest.projectplanner.db.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Insert
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM task_table WHERE isCompleted = :isCompleted")
    suspend fun deleteTaskByIsCompleted(isCompleted: Boolean)

    @Query("DELETE FROM task_table WHERE projectKey= :projectKey")
    suspend fun deleteTaskByProjectKey(projectKey: String)

    @Query("DELETE FROM task_table WHERE (projectKey= :projectKey AND isCompleted= :isCompleted)")
    suspend fun deleteTaskByProjectKeyAndIsCompleted(projectKey: String, isCompleted: Boolean)

    @Query("SELECT * FROM task_table ORDER BY timeStamp DESC")
    fun getAllTasks(): Flow<List<Task>>

    //if hideCompleted is true show all un-completed task and if the hideComplete is false show all tasks
    @Query("SELECT * FROM task_table WHERE projectKey= :projectKey ORDER BY isImportant DESC,isCompleted, timeStamp DESC")
    fun getAllTaskByProjectKey(projectKey: String): Flow<List<Task>>

    @Query("SELECT COUNT(taskKey) FROM task_table WHERE projectKey= :projectKey")
    fun getTaskCountByProjectKey(projectKey: String): LiveData<Int>

}