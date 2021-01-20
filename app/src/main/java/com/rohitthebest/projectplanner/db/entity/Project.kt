package com.rohitthebest.projectplanner.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitthebest.projectplanner.Constants.FALSE

@Entity(tableName = "project_table")
data class Project(
    val timeStamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var modifiedOn: Long = timeStamp,
    var projectName: String,
    var projectProgress: Int = 0,
    var topics: List<Topic>,
    var urls: List<Url>? = null,
    var projectKey: String
) {

    constructor() : this(
        System.currentTimeMillis(),
        null,
        0L,
        "",
        0,
        emptyList(),
        emptyList(),
        ""
    )
}


data class Topic(
    val projectKey: String,
    var topicName: String,
    var isCompleted: String = FALSE,
    var subTopics: List<SubTopic>? = null,
    var topicUrls: List<Url>? = null,
    var markdown: String? = "",
    var topicKey: String
) {

    constructor() : this(
        "",
        "",
        FALSE,
        emptyList(),
        emptyList(),
        "",
        ""
    )
}

data class SubTopic(
    val topicKey: String,
    var subTopicName: String,
    var isCompleted: String = FALSE,
    var subTopicUrls: List<Url>? = null,
) {

    constructor() : this(
        "",
        "",
        FALSE,
        emptyList()
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