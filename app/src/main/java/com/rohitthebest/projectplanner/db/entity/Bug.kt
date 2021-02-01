package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bug_table")
data class Bug(
        var timestamp: Long = System.currentTimeMillis(),
        @PrimaryKey var bugKey: String,
        var projectKey: String,
        var whatBug: String,
        var bugDescription: String,
        var possibleSolution: String?,
        var attachedURL: ArrayList<Url>
) {

    constructor() : this(
            System.currentTimeMillis(),
            "",
            "",
            "",
            "",
            "",
            ArrayList()
    )
}