package com.example.jobtracker.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobtracker.data.DiscoverJob
import com.example.jobtracker.data.DiscoverRepository
import com.example.jobtracker.data.Job
import com.example.jobtracker.data.JobRepository
import com.example.jobtracker.data.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// The three things the screen can show
sealed interface DiscoverUiState {
    data object Loading : DiscoverUiState
    data class Success(val jobs: List<DiscoverJob>, val totalCount: Int = jobs.size) : DiscoverUiState
    data class Error(val message: String) : DiscoverUiState
}

// One download request. The id makes every request different,
// so the StateFlow always emits (needed for "retry").
private data class DownloadRequest(
    val visaOnly: Boolean,
    val isPullToRefresh: Boolean,
    val id: Int
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    // ---------- Inputs from the screen ----------

    private val _searchText = MutableStateFlow("developer")   // starts developer-focused
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _remoteOnly = MutableStateFlow(false)
    val remoteOnly: StateFlow<Boolean> = _remoteOnly.asStateFlow()

    private val _visaOnly = MutableStateFlow(false)
    val visaOnly: StateFlow<Boolean> = _visaOnly.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // ---------- Step 1: download from the API ----------

    private var requestCounter = 0
    private val downloadRequest = MutableStateFlow(
        DownloadRequest(visaOnly = false, isPullToRefresh = false, id = 0)
    )

    // flatMapLatest: a new request cancels the one still running
    private val downloaded: Flow<DiscoverUiState> = downloadRequest.flatMapLatest { request ->
        flow {
            _isRefreshing.value = request.isPullToRefresh
            // Pull-to-refresh shows its own indicator, so skip the full-screen spinner
            if (!request.isPullToRefresh) emit(DiscoverUiState.Loading)

            val result: DiscoverUiState = try {
                DiscoverUiState.Success(discoverRepository.fetchJobs(request.visaOnly))
            } catch (e: CancellationException) {
                throw e                                  // never swallow cancellation
            } catch (e: Exception) {
                DiscoverUiState.Error(friendlyMessage(e))
            }

            _isRefreshing.value = false
            emit(result)
        }
    }

    // ---------- Step 2: search text, waits 300 ms after the last keystroke ----------

    private val debouncedSearch: Flow<String> = _searchText
        .debounce(300)
        .onStart { emit(_searchText.value) }   // first value shows immediately
        .distinctUntilChanged()

    // ---------- Step 3: combine everything into one screen state ----------

    val uiState: StateFlow<DiscoverUiState> =
        combine(downloaded, debouncedSearch, _remoteOnly) { downloadState, search, remote ->
            if (downloadState is DiscoverUiState.Success) {
                DiscoverUiState.Success(
                    jobs = downloadState.jobs.filter { matches(it, search, remote) },
                    totalCount = downloadState.totalCount      // total downloaded, before filtering
                )
            } else {
                downloadState      // Loading or Error passes through unchanged
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,   // starts once, no re-download when switching tabs
            initialValue = DiscoverUiState.Loading
        )

    // Links of jobs already in the tracker (to show "Saved")
    val savedLinks: StateFlow<Set<String>> = jobRepository.getAllJobs()
        .map { jobs -> jobs.map { it.link }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    // ---------- Actions called by the screen ----------

    fun onSearchChange(text: String) {
        _searchText.value = text
    }

    fun toggleRemote() {
        _remoteOnly.value = !_remoteOnly.value
    }

    fun toggleVisa() {
        _visaOnly.value = !_visaOnly.value
        sendRequest(isPull = false)          // the visa filter changes the API call
    }

    fun retry() = sendRequest(isPull = false)

    fun pullToRefresh() = sendRequest(isPull = true)

    fun saveToTracker(job: DiscoverJob) {
        if (job.url in savedLinks.value) return      // already saved
        viewModelScope.launch {
            jobRepository.save(
                Job(
                    company = job.company,
                    role = job.title,
                    link = job.url,
                    status = JobStatus.SAVED,
                    notes = job.location
                )
            )
        }
    }

    // ---------- Helpers ----------

    private fun sendRequest(isPull: Boolean) {
        downloadRequest.value = DownloadRequest(_visaOnly.value, isPull, ++requestCounter)
    }

    // Every word typed must appear somewhere in title, company, location or tags
    private fun matches(job: DiscoverJob, query: String, remoteOnly: Boolean): Boolean {
        if (remoteOnly && !job.remote) return false
        val words = query.trim().split(" ").filter { it.isNotBlank() }
        if (words.isEmpty()) return true
        val text = "${job.title} ${job.company} ${job.location} ${job.tags.joinToString(" ")}"
        return words.all { text.contains(it, ignoreCase = true) }
    }

    private fun friendlyMessage(e: Exception): String = when (e) {
        is IOException -> "No internet connection. Check your network and try again."
        is HttpException -> "The jobs server had a problem (code ${e.code()}). Try again later."
        else -> "Something went wrong while loading jobs."
    }
}