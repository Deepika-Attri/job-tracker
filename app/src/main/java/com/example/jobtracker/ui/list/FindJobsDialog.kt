package com.example.jobtracker.ui.list

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun FindJobsDialog(onDismiss: () -> Unit) {
    // rememberSaveable keeps the text if the screen rotates
    var keywords by rememberSaveable { mutableStateOf("Android Developer") }
    var location by rememberSaveable { mutableStateOf("") }
    var site by rememberSaveable { mutableStateOf(IndeedSite.GLOBAL) }

    val uriHandler = LocalUriHandler.current

    // Opens the link in the browser (runCatching avoids a crash if no browser exists)
    fun open(url: String) {
        runCatching { uriHandler.openUri(url) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Find jobs") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("Keywords") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g. India, Remote)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Indeed country", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IndeedSite.entries.forEach { option ->
                        FilterChip(
                            selected = option == site,
                            onClick = { site = option },
                            label = { Text(option.label) }
                        )
                    }
                }

                Button(
                    onClick = { open(JobSearchLinks.linkedIn(keywords.trim(), location.trim())) },
                    enabled = keywords.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Search on LinkedIn") }

                OutlinedButton(
                    onClick = { open(JobSearchLinks.indeed(site, keywords.trim(), location.trim())) },
                    enabled = keywords.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Search on Indeed") }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}