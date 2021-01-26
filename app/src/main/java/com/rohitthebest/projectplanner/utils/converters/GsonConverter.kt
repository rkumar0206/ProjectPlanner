package com.rohitthebest.projectplanner.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.projectplanner.db.entity.*

class GsonConverter {

    val gson = Gson()

    fun convertProjectToString(project: Project): String = gson.toJson(project)

    fun convertJsonStringToProject(jsonString: String): Project {

        val type = object : TypeToken<Project>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertDescriptionToString(description: Description): String = gson.toJson(description)

    @TypeConverter
    fun convertJsonStringToDescription(jsonString: String): Description {

        val type = object : TypeToken<Description>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertListOfStringToString(list: ArrayList<String>): String = gson.toJson(list)

    @TypeConverter
    fun convertJsonStringToArrayListOfString(jsonString: String): ArrayList<String> {

        val type = object : TypeToken<ArrayList<String>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertResourceToString(resource: Resource?): String? = resource?.let { gson.toJson(it) }

    @TypeConverter
    fun convertJsonStringToResource(jsonString: String): Resource {

        val type = object : TypeToken<Resource>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertThemeToString(theme: Theme?): String? = theme?.let { gson.toJson(it) }

    @TypeConverter
    fun convertJsonStringToTheme(jsonString: String): Theme {

        val type = object : TypeToken<Theme>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertArrayListOfFeaturesToString(features: ArrayList<Feature>): String = gson.toJson(features)

    @TypeConverter
    fun convertJsonStringToArrayListOfFeatures(jsonString: String): ArrayList<Feature> {

        val type = object : TypeToken<ArrayList<Feature>>() {}.type

        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun convertArrayListOfColorsToString(colors: ArrayList<Colors>): String = gson.toJson(colors)

    @TypeConverter
    fun convertJsonStringToArrayListOfColors(jsonString: String): ArrayList<Colors> {

        val type = object : TypeToken<ArrayList<Colors>>() {}.type

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