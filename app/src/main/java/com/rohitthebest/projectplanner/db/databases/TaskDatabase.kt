package com.rohitthebest.projectplanner.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rohitthebest.projectplanner.db.dao.TaskDao
import com.rohitthebest.projectplanner.db.entity.Task

@Database(
        entities = [Task::class],
        version = 1
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao
}