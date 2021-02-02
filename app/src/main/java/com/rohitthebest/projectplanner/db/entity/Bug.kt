package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitthebest.projectplanner.Constants.FALSE

@Entity(tableName = "bug_table")
data class Bug(
        var timestamp: Long = System.currentTimeMillis(),
        @PrimaryKey var bugKey: String,
        var projectKey: String,
        var bugDescription: String,
        var possibleSolution: String?,
        var isResolved: String = FALSE,
) {

    constructor() : this(
            System.currentTimeMillis(),
            "",
            "",
            "",
            "",
            FALSE
    )
}