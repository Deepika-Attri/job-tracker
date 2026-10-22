package com.example.jobtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Job::class], version = 1, exportSchema = false)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
}