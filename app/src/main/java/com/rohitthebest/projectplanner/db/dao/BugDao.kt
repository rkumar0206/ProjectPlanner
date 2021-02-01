package com.rohitthebest.projectplanner.db.dao

import androidx.room.*
import com.rohitthebest.projectplanner.db.entity.Bug
import kotlinx.coroutines.flow.Flow

@Dao
interface BugDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBug(bug: Bug)

    @Insert
    suspend fun insertBugs(bugs: List<Bug>)

    @Update
    suspend fun updateBug(bug: Bug)

    @Delete
    suspend fun deleteBug(bug: Bug)

    @Query("DELETE FROM bug_table")
    suspend fun deleteAllBug()

    @Query("DELETE FROM bug_table WHERE projectKey= :projectKey")
    suspend fun deleteByProjectKey(projectKey: String)

    @Query("SELECT * FROM bug_table ORDER BY timestamp DESC")
    fun getAllBugList(): Flow<List<Bug>>

    @Query("SELECT * FROM bug_table WHERE projectKey= :projectKey ORDER BY timestamp DESC")
    fun getAllBugsByProjectKey(projectKey: String): Flow<List<Bug>>

}