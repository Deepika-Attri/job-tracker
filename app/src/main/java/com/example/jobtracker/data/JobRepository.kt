package com.example.jobtracker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JobRepository @Inject constructor(private val dao: JobDao) {

    fun getAllJobs(): Flow<List<Job>> = dao.getAllJobs()

    fun observeJob(id: Int): Flow<Job?> = dao.observeJob(id)

    suspend fun getJob(id: Int): Job? = dao.getJob(id)

    suspend fun save(job: Job) = dao.upsert(job)

    suspend fun delete(job: Job) = dao.delete(job)
}