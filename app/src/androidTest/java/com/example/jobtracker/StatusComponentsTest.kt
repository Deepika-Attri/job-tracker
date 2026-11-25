package com.example.jobtracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.jobtracker.data.JobStatus
import com.example.jobtracker.ui.components.StatusChip
import com.example.jobtracker.ui.components.StatusSelector
import com.example.jobtracker.ui.theme.JobTrackerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StatusComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statusChip_showsTheStatusLabel() {
        composeRule.setContent { JobTrackerTheme { StatusChip(JobStatus.INTERVIEW) } }

        composeRule.onNodeWithText("Interview").assertIsDisplayed()
    }

    @Test
    fun statusSelector_reportsTheClickedStatus() {
        var selected = JobStatus.APPLIED
        composeRule.setContent {
            JobTrackerTheme {
                StatusSelector(selected = selected, onSelected = { selected = it })
            }
        }

        composeRule.onNodeWithText("Offer").performClick()

        assertEquals(JobStatus.OFFER, selected)
    }
}