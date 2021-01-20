package com.rohitthebest.projectplanner.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rohitthebest.projectplanner.db.dao.ProjectDao
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.utils.converters.GsonConverter

@Database(entities = [Project::class], version = 1)
@TypeConverters(GsonConverter::class)
abstract class ProjectDatabase : RoomDatabase() {

    abstract fun getProjectDao(): ProjectDao
}