package com.example.jobtracker.data

import androidx.core.text.HtmlCompat
import com.example.jobtracker.data.remote.JobDto

data class DiscoverJob(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val remote: Boolean,
    val url: String,
    val tags: List<String>,
    val preview: String,      // short plain-text description
    val postedAt: Long        // milliseconds
)

// Converts the network model (DTO) into the UI model
fun JobDto.toDiscoverJob() = DiscoverJob(
    id = slug.ifBlank { url },
    title = title.trim(),
    company = companyName.trim(),
    location = location.trim(),
    remote = remote,
    url = url,
    tags = tags,
    preview = htmlToPreview(description),
    postedAt = createdAt * 1000       // API sends seconds, Android uses milliseconds
)

// HTML -> plain text. The API's HTML can be escaped, so we decode twice,
// then remove any leftover tags and extra whitespace.
private fun htmlToPreview(html: String): String {
    var text = html
    repeat(2) {
        text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }
    return text
        .replace("\uFFFC", "")                // placeholder char for images
        .replace(Regex("<[^>]*>"), " ")       // leftover tags
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(220)
}