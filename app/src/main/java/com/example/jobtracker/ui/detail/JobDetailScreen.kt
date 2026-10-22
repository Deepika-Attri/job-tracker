package com.example.jobtracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jobtracker.ui.components.StatusChip
import com.example.jobtracker.ui.components.StatusSelector
import com.example.jobtracker.ui.components.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val job by viewModel.job.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show edit/delete only once the job has loaded
                    job?.let { current ->
                        IconButton(onClick = { onEdit(current.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        job?.let { current ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(current.company, style = MaterialTheme.typography.headlineMedium)
                Text(current.role, style = MaterialTheme.typography.titleMedium)
                StatusChip(current.status)
                Text("Applied on ${formatDate(current.dateApplied)}")

                // Open the link in the browser (ignore bad links instead of crashing)
                if (current.link.isNotBlank()) {
                    TextButton(onClick = { runCatching { uriHandler.openUri(current.link) } }) {
                        Text("Open job link")
                    }
                }

                if (current.notes.isNotBlank()) {
                    Text("Notes", style = MaterialTheme.typography.labelLarge)
                    Text(current.notes)
                }

                Text("Change status", style = MaterialTheme.typography.labelLarge)
                StatusSelector(
                    selected = current.status,
                    onSelected = { viewModel.updateStatus(it) }
                )
            }
        }
    }

    // Confirmation popup before deleting
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this application?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(onBack)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}