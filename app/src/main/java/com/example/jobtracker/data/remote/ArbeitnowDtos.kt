package com.example.jobtracker.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The whole response: { "data": [ ...jobs... ], "links": ..., "meta": ... }
// We only need "data", and the rest is ignored (see ignoreUnknownKeys in NetworkModule)
@Serializable
data class JobsResponse(
    val data: List<JobDto> = emptyList()
)

// One job as the API sends it
@Serializable
data class JobDto(
    val slug: String = "",
    @SerialName("company_name") val companyName: String = "",
    val title: String = "",
    val description: String = "",            // HTML text, we clean it later
    val remote: Boolean = false,
    val url: String = "",                    // link to the original posting
    val tags: List<String> = emptyList(),
    @SerialName("job_types") val jobTypes: List<String> = emptyList(),
    val location: String = "",
    @SerialName("created_at") val createdAt: Long = 0   // seconds since 1970
)