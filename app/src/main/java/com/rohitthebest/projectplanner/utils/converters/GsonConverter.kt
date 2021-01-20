package com.rohitthebest.projectplanner.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.projectplanner.db.entity.Project
import com.rohitthebest.projectplanner.db.entity.SubTopic
import com.rohitthebest.projectplanner.db.entity.Topic
import com.rohitthebest.projectplanner.db.entity.Url

class GsonConverter {

    val gson = Gson()

    fun convertProjectToString(project: Project): String = gson.toJson(project)

    fun convertJsonStringToProject(jsonString: String): Project {

        val type = object : TypeToken<Project>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertTopicToString(topic: Topic): String = gson.toJson(topic)

    @TypeConverter
    fun convertJsonStringToTopic(jsonString: String): Topic {

        val type = object : TypeToken<Topic>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertSubTopicToString(subTopic: SubTopic): String = gson.toJson(subTopic)

    @TypeConverter
    fun convertJsonStringToSubTopic(jsonString: String): SubTopic {

        val type = object : TypeToken<SubTopic>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertUrlToString(url: Url): String = gson.toJson(url)

    @TypeConverter
    fun convertJsonStringToUrl(jsonString: String): Url {

        val type = object : TypeToken<Url>() {}.type

        return gson.fromJson(jsonString, type)
    }

}