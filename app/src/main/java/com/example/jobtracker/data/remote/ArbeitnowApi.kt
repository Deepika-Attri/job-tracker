package com.example.jobtracker.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ArbeitnowApi {

    // GET https://www.arbeitnow.com/api/job-board-api
    // With visaSponsorship = true the URL gets ?visa_sponsorship=true
    @GET("job-board-api")
    suspend fun getJobs(
        @Query("visa_sponsorship") visaSponsorship: Boolean? = null
    ): JobsResponse
}