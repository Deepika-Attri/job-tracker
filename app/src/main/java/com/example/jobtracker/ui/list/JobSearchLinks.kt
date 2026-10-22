package com.example.jobtracker.ui.list

import android.net.Uri

// Indeed has a separate website per country
enum class IndeedSite(val label: String, val domain: String) {
    GLOBAL("Global", "www.indeed.com"),
    INDIA("India", "in.indeed.com"),
    GERMANY("Germany", "de.indeed.com"),
    NETHERLANDS("Netherlands", "nl.indeed.com")
}

object JobSearchLinks {

    // Uri.encode turns spaces and special characters into URL-safe text
    fun linkedIn(keywords: String, location: String): String =
        "https://www.linkedin.com/jobs/search/" +
                "?keywords=${Uri.encode(keywords)}&location=${Uri.encode(location)}"

    fun indeed(site: IndeedSite, keywords: String, location: String): String =
        "https://${site.domain}/jobs?q=${Uri.encode(keywords)}&l=${Uri.encode(location)}"
}