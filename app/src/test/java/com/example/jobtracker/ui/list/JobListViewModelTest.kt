package com.example.jobtracker.ui.list

import com.example.jobtracker.data.Job
import com.example.jobtracker.data.JobRepository
import com.example.jobtracker.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class JobListViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `jobs shows the list coming from the repository`() = runTest {
        val jobs = listOf(
            Job(id = 1, company = "Acme", role = "Android Developer"),
            Job(id = 2, company = "Globex", role = "Kotlin Developer")
        )
        val repository = mockk<JobRepository>()
        every { repository.getAllJobs() } returns flowOf(jobs)

        val viewModel = JobListViewModel(repository)

        // The StateFlow starts empty, so wait for the first non-empty list
        assertEquals(jobs, viewModel.jobs.first { it.isNotEmpty() })
    }
}