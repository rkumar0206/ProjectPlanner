package com.rohitthebest.projectplanner.module

import android.content.Context
import androidx.room.Room
import com.rohitthebest.projectplanner.Constants.BUG_DATABASE_NAME
import com.rohitthebest.projectplanner.Constants.PROJECT_DATABASE_NAME
import com.rohitthebest.projectplanner.Constants.TASK_DATABASE_NAME
import com.rohitthebest.projectplanner.db.databases.BugDatabase
import com.rohitthebest.projectplanner.db.databases.ProjectDatabase
import com.rohitthebest.projectplanner.db.databases.TaskDatabase
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


    @Provides
    @Singleton
    fun providesTaskDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            TASK_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesTaskDao(db: TaskDatabase) = db.getTaskDao()


    @Provides
    @Singleton
    fun providesBugDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            BugDatabase::class.java,
            BUG_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesBugDao(db: BugDatabase) = db.getBugDao()
}