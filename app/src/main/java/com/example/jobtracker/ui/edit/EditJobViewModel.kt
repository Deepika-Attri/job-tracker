package com.example.jobtracker.ui.edit

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobtracker.data.Job
import com.example.jobtracker.data.JobRepository
import com.example.jobtracker.data.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditJobViewModel @Inject constructor(
    private val repository: JobRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: Int = checkNotNull(savedStateHandle["jobId"])
    val isEditing = jobId != -1

    var company by mutableStateOf("")
    var role by mutableStateOf("")
    var link by mutableStateOf("")
    var notes by mutableStateOf("")
    var status by mutableStateOf(JobStatus.APPLIED)

    private var dateApplied = System.currentTimeMillis()

    private var originalStatus: JobStatus? = null

    // Link is optional. If filled, it must look like a real web address.
    val isLinkValid: Boolean
        get() = link.isBlank() || Patterns.WEB_URL.matcher(normalizeLink(link)).matches()

    // Save is allowed only when required fields are filled and the link is valid
    val canSave: Boolean
        get() = company.isNotBlank() && role.isNotBlank() && isLinkValid

    // Adds "https://" if the user forgot it, so "company.com/jobs" becomes a proper link
    private fun normalizeLink(raw: String): String {
        val text = raw.trim()
        if (text.isEmpty()) return ""
        val hasScheme = text.startsWith("http://", ignoreCase = true) ||
                text.startsWith("https://", ignoreCase = true)
        return if (hasScheme) text else "https://$text"
    }

    init {
        if (isEditing) {
            viewModelScope.launch {
                repository.getJob(jobId)?.let { job ->
                    company = job.company
                    role = job.role
                    link = job.link
                    notes = job.notes
                    status = job.status
                    dateApplied = job.dateApplied
                    originalStatus = job.status
                }
            }
        }
    }

    fun save(onDone: () -> Unit) {
        if (!canSave) return
        viewModelScope.launch {
            repository.save(
                Job(
                    id = if (isEditing) jobId else 0,
                    company = company.trim(),
                    role = role.trim(),
                    link = normalizeLink(link), // saved with https:// added
                    status = status,
                    dateApplied = if (originalStatus == JobStatus.SAVED && status != JobStatus.SAVED)
                        System.currentTimeMillis() else dateApplied,
                    notes = notes.trim()
                )
            )
            onDone()
        }
    }
}