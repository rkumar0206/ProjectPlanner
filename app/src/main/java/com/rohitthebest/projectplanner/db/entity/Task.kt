package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
        var timeStamp: Long = System.currentTimeMillis(),
        @PrimaryKey var taskKey: String = "",
        var projectKey: String = "",
        var taskName: String,
        var taskDescription: String? = null,
        var isCompleted: Boolean = false,
        var isImportant: Boolean = false
) {

    constructor() : this(
            System.currentTimeMillis(),
            "",
            "",
            "",
            "",
            false,
            false
    )
}

