package com.rohitthebest.projectplanner.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rohitthebest.projectplanner.db.entity.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert
    suspend fun insertProjects(projects: List<Project>)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM project_table")
    suspend fun deleteAllProjects()

    @Query("SELECT * FROM project_table ORDER BY modifiedOn DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM project_table WHERE projectKey= :projectKey")
    fun getProjectByProjectKey(projectKey: String): LiveData<Project>
}