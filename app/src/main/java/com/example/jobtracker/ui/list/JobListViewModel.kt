package com.example.jobtracker.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobtracker.data.Job
import com.example.jobtracker.data.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class JobListViewModel @Inject constructor(
    repository: JobRepository
) : ViewModel() {

    // Always holds the latest list from the database (empty list at first)
    val jobs: StateFlow<List<Job>> = repository.getAllJobs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // stop work 5s after screen leaves
            initialValue = emptyList()
        )
}