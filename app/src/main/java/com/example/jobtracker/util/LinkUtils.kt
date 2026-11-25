package com.example.jobtracker.util

import java.net.URI
import java.net.URISyntaxException

object LinkUtils {

    // Adds "https://" when the user forgot it
    fun normalize(raw: String): String {
        val text = raw.trim()
        if (text.isEmpty()) return ""
        val hasScheme = text.startsWith("http://", ignoreCase = true) ||
                text.startsWith("https://", ignoreCase = true)
        return if (hasScheme) text else "https://$text"
    }

    // Empty is fine (the link is optional). Otherwise it needs a real host like "company.com"
    fun isValid(raw: String): Boolean {
        if (raw.isBlank()) return true
        val normalized = normalize(raw)
        if (normalized.contains(" ")) return false
        return try {
            val host = URI(normalized).host
            host != null && host.contains(".") && !host.startsWith(".") && !host.endsWith(".")
        } catch (e: URISyntaxException) {
            false
        }
    }
}