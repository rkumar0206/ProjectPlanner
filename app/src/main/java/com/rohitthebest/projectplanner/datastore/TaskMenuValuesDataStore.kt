package com.rohitthebest.projectplanner.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.asLiveData
import com.rohitthebest.projectplanner.Constants.SORTING_DATASTORE_NAME
import com.rohitthebest.projectplanner.Constants.SORT_BY_DATE_DESC
import kotlinx.coroutines.flow.map

data class TaskMenuValues(
        var sortingMethod: String,
        var hideCompletedTask: Boolean
)

class TaskMenuValuesDataStore(context: Context) {

    private val dataStore = context.createDataStore(SORTING_DATASTORE_NAME)

    companion object {

        val SORTING_KEY = stringPreferencesKey("sorting_key")
        val HIDE_COMPLETED_KEY = booleanPreferencesKey("hide_completed_key")
    }

    suspend fun storeSortingMethod(
            taskMenuValues: TaskMenuValues
    ) {

        dataStore.edit {

            it[SORTING_KEY] = taskMenuValues.sortingMethod
            it[HIDE_COMPLETED_KEY] = taskMenuValues.hideCompletedTask
        }
    }

    val sortingFlow = dataStore.data.map {

        val sortingMethod = it[SORTING_KEY] ?: SORT_BY_DATE_DESC
        val hideCompleted = it[HIDE_COMPLETED_KEY] ?: false

        TaskMenuValues(
                sortingMethod,
                hideCompleted
        )
    }.asLiveData()
}
