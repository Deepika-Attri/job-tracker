package com.example.jobtracker.data

import com.example.jobtracker.data.remote.ArbeitnowApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepository @Inject constructor(
    private val api: ArbeitnowApi
) {
    suspend fun fetchJobs(visaOnly: Boolean): List<DiscoverJob> {
        // Passing null leaves the parameter out of the URL
        val response = api.getJobs(visaSponsorship = if (visaOnly) true else null)

        return withContext(Dispatchers.Default) {
            response.data
                .filter { it.title.isNotBlank() && it.url.isNotBlank() }
                .map { it.toDiscoverJob() }
                .distinctBy { it.id }        // list keys must be unique
        }
    }
}