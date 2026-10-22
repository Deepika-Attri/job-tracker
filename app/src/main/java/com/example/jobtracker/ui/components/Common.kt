package com.example.jobtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jobtracker.data.JobStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Small colored pill showing the status
@Composable
fun StatusChip(status: JobStatus) {
    val color = when (status) {
        JobStatus.SAVED -> Color(0xFF757575)
        JobStatus.APPLIED -> Color(0xFF1E88E5)
        JobStatus.INTERVIEW -> Color(0xFFFB8C00)
        JobStatus.OFFER -> Color(0xFF43A047)
        JobStatus.REJECTED -> Color(0xFFE53935)
    }
    Text(
        text = status.label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

// Horizontal row of selectable chips, one per status
@Composable
fun StatusSelector(selected: JobStatus, onSelected: (JobStatus) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobStatus.entries.forEach { status ->
            FilterChip(
                selected = status == selected,
                onClick = { onSelected(status) },
                label = { Text(status.label) }
            )
        }
    }
}

// Milliseconds -> "20 Oct 2026"
fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))