package com.rohitthebest.projectplanner.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rohitthebest.projectplanner.db.dao.BugDao
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.utils.converters.GsonConverter

@Database(
        entities = [Bug::class],
        version = 1
)
@TypeConverters(GsonConverter::class)
abstract class BugDatabase : RoomDatabase() {

    abstract fun getBugDao(): BugDao
}