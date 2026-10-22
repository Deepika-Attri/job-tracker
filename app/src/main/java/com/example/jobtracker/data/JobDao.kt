package com.example.jobtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    // Live list of all jobs, newest first
    @Query("SELECT * FROM jobs ORDER BY dateApplied DESC")
    fun getAllJobs(): Flow<List<Job>>

    // Live single job (used by the detail screen)
    @Query("SELECT * FROM jobs WHERE id = :id")
    fun observeJob(id: Int): Flow<Job?>

    // One-time read (used by the edit screen to fill the form)
    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJob(id: Int): Job?

    // Insert if new (id = 0), update if the id already exists
    @Upsert
    suspend fun upsert(job: Job)

    @Delete
    suspend fun delete(job: Job)
}