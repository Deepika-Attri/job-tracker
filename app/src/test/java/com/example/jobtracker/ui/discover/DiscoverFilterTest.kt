package com.example.jobtracker.ui.discover

import com.example.jobtracker.data.DiscoverJob
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverFilterTest {

    // Helper that builds a job with sensible defaults
    private fun job(
        title: String = "Android Developer",
        company: String = "Acme GmbH",
        location: String = "Berlin",
        remote: Boolean = false,
        tags: List<String> = listOf("kotlin")
    ) = DiscoverJob(
        id = "1", title = title, company = company, location = location,
        remote = remote, url = "https://example.com", tags = tags,
        preview = "", postedAt = 0
    )

    @Test
    fun `empty search matches every job`() {
        assertTrue(matchesFilters(job(), query = "", remoteOnly = false))
    }

    @Test
    fun `search ignores upper and lower case`() {
        assertTrue(matchesFilters(job(), query = "ANDROID", remoteOnly = false))
    }

    @Test
    fun `all typed words must match`() {
        assertTrue(matchesFilters(job(), query = "android berlin", remoteOnly = false))
        assertFalse(matchesFilters(job(), query = "android munich", remoteOnly = false))
    }

    @Test
    fun `search also looks at tags and company`() {
        assertTrue(matchesFilters(job(), query = "kotlin", remoteOnly = false))
        assertTrue(matchesFilters(job(), query = "acme", remoteOnly = false))
    }

    @Test
    fun `remote filter hides on-site jobs`() {
        assertFalse(matchesFilters(job(remote = false), query = "", remoteOnly = true))
        assertTrue(matchesFilters(job(remote = true), query = "", remoteOnly = true))
    }
}