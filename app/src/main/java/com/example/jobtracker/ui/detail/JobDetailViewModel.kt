package com.example.jobtracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobtracker.data.Job
import com.example.jobtracker.data.JobRepository
import com.example.jobtracker.data.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val repository: JobRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: Int = checkNotNull(savedStateHandle["jobId"])

    // Live job. Null while loading (or after it is deleted).
    val job: StateFlow<Job?> = repository.observeJob(jobId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Change only the status, keep everything else
    fun updateStatus(status: JobStatus) {
        val current = job.value ?: return
        viewModelScope.launch { repository.save(current.copy(status = status)) }
    }

    fun delete(onDone: () -> Unit) {
        val current = job.value ?: return
        viewModelScope.launch {
            repository.delete(current)
            onDone()
        }
    }
}