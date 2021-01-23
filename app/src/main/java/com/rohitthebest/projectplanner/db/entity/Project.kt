package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitthebest.projectplanner.Constants.FALSE

@Entity(tableName = "project_table")
data class Project(
        val timeStamp: Long = System.currentTimeMillis(),
        var modifiedOn: Long = timeStamp,
        var projectName: String,
        var projectProgress: Int = 0,
        var topics: ArrayList<Topic>,
        var urls: ArrayList<Url>? = null,
        @PrimaryKey var projectKey: String,
        var markDown: String? = null
) {

    constructor() : this(
            System.currentTimeMillis(),
            0L,
            "",
            0,
            ArrayList(),
            ArrayList(),
            "",
            ""
    )
}


data class Topic(
    val projectKey: String,
    var topicName: String,
    var isCompleted: String = FALSE,
    var subTopics: ArrayList<SubTopic>? = null,
    var topicUrls: ArrayList<Url>? = null,
    var markdown: String? = "",
    var topicKey: String
) {

    constructor() : this(
        "",
        "",
        FALSE,
        ArrayList(),
        ArrayList(),
        "",
        ""
    )
}

data class SubTopic(
        val topicKey: String,
        var subTopicName: String,
        var isCompleted: String = FALSE,
        var subTopicUrls: ArrayList<Url>? = null,
        var subTopicKey: String
) {

    constructor() : this(
            "",
            "",
            FALSE,
            ArrayList(),
            ""
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