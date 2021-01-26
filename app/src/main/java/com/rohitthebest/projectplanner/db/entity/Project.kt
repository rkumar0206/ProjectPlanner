package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class Project(
        val timeStamp: Long = System.currentTimeMillis(),
        var modifiedOn: Long = timeStamp,
        @PrimaryKey var projectKey: String,
        var description: Description,
        var features: ArrayList<Feature>,
        var problemSolved: String,
        var skillsRequired: ArrayList<String>,
        var technologyUsed: ArrayList<String>,
        var estimatedTimeForCompleting: Long? = null,
        var resources: Resource? = null,
        var theme: Theme? = null,
        var iconLink: String? = null
) {

    constructor() : this(
            System.currentTimeMillis(),
            0L,
            "",
            Description(),
            ArrayList(),
            "",
            ArrayList(),
            ArrayList(),
            0L,
            Resource(),
            Theme(),
            ""
    )
}

data class Description(
        var name: String,
        var desc: String
) {
    constructor() : this(
            "",
            ""
    )
}

data class Feature(
        var name: String,
        var description: String,
        var implementation: String
) {

    constructor() : this(
            "",
            "",
            ""
    )
}

data class Resource(
        var urls: ArrayList<Url>,
        var photos: ArrayList<String>
) {

    constructor() : this(
            ArrayList(),
            ArrayList()
    )
}

data class Url(
        var urlName: String?,
        var url: String
) {

    constructor() : this(
            "",
            ""
    )
}

data class Theme(
        var primaryColor: String,
        var darkPrimaryColor: String,
        var accentColor: String,
        var primaryTextColor: String,
        var secondaryTextColor: String,
        var textColorOnPrimaryColor: String
) {

    constructor() : this(
            "",
            "",
            "",
            "",
            "",
            ""
    )
}