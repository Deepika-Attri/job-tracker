package com.example.jobtracker

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import com.example.jobtracker.ui.list.FindJobsDialog
import com.example.jobtracker.ui.theme.JobTrackerTheme
import org.junit.Rule
import org.junit.Test

class FindJobsDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun searchButtons_areDisabledWhenKeywordsAreEmpty() {
        composeRule.setContent { JobTrackerTheme { FindJobsDialog(onDismiss = {}) } }

        // The field starts with "Android Developer", so the buttons are enabled
        composeRule.onNodeWithText("Search on LinkedIn").assertIsEnabled()

        // Clear the keywords and the buttons should switch off
        composeRule.onNodeWithText("Android Developer").performTextClearance()
        composeRule.onNodeWithText("Search on LinkedIn").assertIsNotEnabled()
        composeRule.onNodeWithText("Search on Indeed").assertIsNotEnabled()
    }
}