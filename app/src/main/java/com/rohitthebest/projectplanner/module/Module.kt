package com.rohitthebest.projectplanner.module

import android.content.Context
import androidx.room.Room
import com.rohitthebest.projectplanner.Constants.PROJECT_DATABASE_NAME
import com.rohitthebest.projectplanner.db.databases.ProjectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object Module {

    @Provides
    @Singleton
    fun providesProjectDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ProjectDatabase::class.java,
        PROJECT_DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesProjectDao(db: ProjectDatabase) = db.getProjectDao()
}