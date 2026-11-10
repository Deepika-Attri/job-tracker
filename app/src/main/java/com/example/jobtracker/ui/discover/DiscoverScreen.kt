package com.example.jobtracker.ui.discover

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jobtracker.data.DiscoverJob
import com.example.jobtracker.ui.components.openLink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(viewModel: DiscoverViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val remoteOnly by viewModel.remoteOnly.collectAsStateWithLifecycle()
    val visaOnly by viewModel.visaOnly.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val savedLinks by viewModel.savedLinks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Discover jobs", style = MaterialTheme.typography.titleLarge) })
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)   // bottom bar handles the bottom inset
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Search box with a clear (X) button
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("Search title, company, skill") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filter chips
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = remoteOnly,
                    onClick = viewModel::toggleRemote,
                    label = { Text("Remote") }
                )
                FilterChip(
                    selected = visaOnly,
                    onClick = viewModel::toggleVisa,
                    label = { Text("Visa sponsorship") }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Pull down to refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::pullToRefresh,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // The three states of the sealed interface
                when (val current = state) {
                    is DiscoverUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is DiscoverUiState.Error -> {
                        CenterMessage(
                            text = current.message,
                            actionLabel = "Retry",
                            onAction = viewModel::retry
                        )
                    }

                    is DiscoverUiState.Success -> {
                        if (current.jobs.isEmpty()) {
                            CenterMessage(
                                text = "No jobs match your search.\nTry another keyword or clear the filters."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        "Showing ${current.jobs.size} of ${current.totalCount} jobs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                items(current.jobs, key = { it.id }) { job ->
                                    DiscoverJobCard(
                                        job = job,
                                        isSaved = job.url in savedLinks,
                                        onOpen = { openLink(context, job.url) },
                                        onSave = { viewModel.saveToTracker(job) },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                // Credit the data source
                                item {
                                    TextButton(
                                        onClick = { openLink(context, "https://www.arbeitnow.com") },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Jobs provided by Arbeitnow") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// One job card
@Composable
private fun DiscoverJobCard(
    job: DiscoverJob,
    isSaved: Boolean,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onOpen, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                job.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                job.company,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                listOf(job.location, postedAgo(job.postedAt)).filter { it.isNotBlank() }.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (job.preview.isNotBlank()) {
                Text(
                    job.preview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Remote badge + first few tags
            if (job.remote || job.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (job.remote) Tag("Remote")
                    job.tags.take(3).forEach { Tag(it) }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (isSaved) {
                    Text(
                        "Saved ✓",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    FilledTonalButton(onClick = onSave) { Text("Save to tracker") }
                }
            }
        }
    }
}

// Small rounded label
@Composable
private fun Tag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        maxLines = 1,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

// Centered message with an optional button (used for empty and error states)
@Composable
private fun CenterMessage(
    text: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

// 1700000000000 -> "3 days ago"
private fun postedAgo(millis: Long): String =
    if (millis <= 0) ""
    else DateUtils.getRelativeTimeSpanString(
        millis, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS
    ).toString()