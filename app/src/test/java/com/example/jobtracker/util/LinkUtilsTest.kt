package com.example.jobtracker.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkUtilsTest {

    @Test
    fun `empty link is valid because the field is optional`() {
        assertTrue(LinkUtils.isValid(""))
        assertTrue(LinkUtils.isValid("   "))
    }

    @Test
    fun `link without scheme gets https added`() {
        assertEquals("https://company.com/jobs/1", LinkUtils.normalize("company.com/jobs/1"))
    }

    @Test
    fun `link that already has a scheme is kept`() {
        assertEquals("http://company.com", LinkUtils.normalize("http://company.com"))
    }

    @Test
    fun `normal links are valid`() {
        assertTrue(LinkUtils.isValid("company.com/jobs/1"))
        assertTrue(LinkUtils.isValid("https://www.linkedin.com/jobs/view/123"))
    }

    @Test
    fun `text with spaces is invalid`() {
        assertFalse(LinkUtils.isValid("hello world"))
    }

    @Test
    fun `word without a domain is invalid`() {
        assertFalse(LinkUtils.isValid("hello"))
    }
}