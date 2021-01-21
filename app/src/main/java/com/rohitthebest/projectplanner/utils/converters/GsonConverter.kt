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
    fun convertListOfTopicToString(topics: ArrayList<Topic>): String = gson.toJson(topics)

    @TypeConverter
    fun convertJsonStringToTopicList(jsonString: String): ArrayList<Topic> {

        val type = object : TypeToken<ArrayList<Topic>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertListOfSubTopicToString(subTopics: ArrayList<SubTopic>): String = gson.toJson(subTopics)

    @TypeConverter
    fun convertJsonStringToSubTopicList(jsonString: String): ArrayList<SubTopic> {

        val type = object : TypeToken<ArrayList<SubTopic>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertListOfUrlToString(urls: ArrayList<Url>): String = gson.toJson(urls)

    @TypeConverter
    fun convertJsonStringToUrlList(jsonString: String): ArrayList<Url> {

        val type = object : TypeToken<ArrayList<Url>>() {}.type

        return gson.fromJson(jsonString, type)
    }

}