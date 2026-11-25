package com.example.jobtracker.data

import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class JobRepositoryTest {

    // A fake DAO: no real database needed
    private val dao = mockk<JobDao>(relaxed = true)
    private val repository = JobRepository(dao)

    @Test
    fun `save sends the job to the dao`() = runTest {
        val job = Job(company = "Acme", role = "Android Developer")

        repository.save(job)

        coVerify(exactly = 1) { dao.upsert(job) }
    }

    @Test
    fun `delete sends the job to the dao`() = runTest {
        val job = Job(id = 5, company = "Acme", role = "Android Developer")

        repository.delete(job)

        coVerify(exactly = 1) { dao.delete(job) }
    }

    @Test
    fun `getAllJobs gives back what the dao emits`() = runTest {
        val jobs = listOf(Job(id = 1, company = "Acme", role = "Dev"))
        every { dao.getAllJobs() } returns flowOf(jobs)

        // Turbine: collect the Flow and check each item in order
        repository.getAllJobs().test {
            assertEquals(jobs, awaitItem())
            awaitComplete()
        }
    }
}