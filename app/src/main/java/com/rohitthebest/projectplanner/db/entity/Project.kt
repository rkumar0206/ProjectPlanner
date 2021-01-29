package com.rohitthebest.projectplanner.db.entity

import android.graphics.Color
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
        var technologyUsed: ArrayList<Technology>,
        var estimatedTimeForCompleting: Long? = null,
        var resources: Resource? = null,
        var theme: Theme? = null,
        var colors : ArrayList<Colors>,
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
            ArrayList(),
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
        var urls: ArrayList<Url>
) {

    constructor() : this(
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

data class Technology(
        val name: String,
        val backgroundColor: Int? = Color.WHITE,
        var textColor: Int? = Color.BLACK
) {

        constructor() : this(
                "",
                Color.WHITE,
                Color.BLACK
        )
}

data class Colors(
        var colorName: String,
        var colorHexCode: String
) {

        constructor() : this(
                "",
                ""
        )
}