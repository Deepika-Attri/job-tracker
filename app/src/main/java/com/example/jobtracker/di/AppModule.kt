package com.example.jobtracker.di

import android.content.Context
import androidx.room.Room
import com.example.jobtracker.data.JobDao
import com.example.jobtracker.data.JobDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // lives as long as the app
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JobDatabase =
        Room.databaseBuilder(context, JobDatabase::class.java, "job_tracker_db").build()

    @Provides
    fun provideJobDao(database: JobDatabase): JobDao = database.jobDao()
}