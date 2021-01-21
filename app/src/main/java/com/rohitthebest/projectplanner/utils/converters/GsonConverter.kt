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
    fun convertListOfTopicToString(topics: List<Topic>): String = gson.toJson(topics)

    @TypeConverter
    fun convertJsonStringToTopicList(jsonString: String): List<Topic> {

        val type = object : TypeToken<List<Topic>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertListOfSubTopicToString(subTopics: List<SubTopic>): String = gson.toJson(subTopics)

    @TypeConverter
    fun convertJsonStringToSubTopicList(jsonString: String): List<SubTopic> {

        val type = object : TypeToken<List<SubTopic>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertListOfUrlToString(urls: List<Url>): String = gson.toJson(urls)

    @TypeConverter
    fun convertJsonStringToUrlList(jsonString: String): List<Url> {

        val type = object : TypeToken<List<Url>>() {}.type

        return gson.fromJson(jsonString, type)
    }

}