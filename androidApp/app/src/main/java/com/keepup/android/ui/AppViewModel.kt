package com.keepup.android.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.keepup.android.BuildConfig
import com.keepup.android.auth.AuthManager
import com.keepup.android.data.Review
import com.keepup.android.data.ReviewSummary
import com.keepup.android.data.Show
import com.keepup.android.data.ShowRepository
import com.keepup.android.data.ShowType
import com.keepup.android.firestore.FirestoreManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = ShowRepository(app, apiKey = BuildConfig.TMDB_API_KEY ?: "")
    private val authManager = AuthManager(app)
    private val firestoreManager = FirestoreManager()

    // Auth state
    val currentUser: StateFlow<FirebaseUser?> = authManager.authStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authManager.currentUser)

    val isSignedIn: StateFlow<Boolean> = currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authManager.isSignedIn)

    // Tracked shows
    val trackedShows: StateFlow<List<Show>> = repository.trackedShowsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Search state
    private val _searchResults = MutableStateFlow<List<Show>>(emptyList())
    val searchResults: StateFlow<List<Show>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError

    var searchType: ShowType = ShowType.SERIES
    var searchText: String = ""

    // Updates feed
    private val _updates = MutableStateFlow<List<Show>>(emptyList())
    val updates: StateFlow<List<Show>> = _updates

    private val _isLoadingUpdates = MutableStateFlow(false)
    val isLoadingUpdates: StateFlow<Boolean> = _isLoadingUpdates

    // Selected show for detail view
    private val _selectedShow = MutableStateFlow<Show?>(null)
    val selectedShow: StateFlow<Show?> = _selectedShow

    // Reviews for selected show
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _reviewSummary = MutableStateFlow<ReviewSummary?>(null)
    val reviewSummary: StateFlow<ReviewSummary?> = _reviewSummary

    // Navigation state
    private val _selectedTab = MutableStateFlow("updates")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _shouldFocusDiscoverSearch = MutableStateFlow(false)
    val shouldFocusDiscoverSearch: StateFlow<Boolean> = _shouldFocusDiscoverSearch

    init {
        // Load tracked shows from Firestore when user signs in
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null && user.isEmailVerified) {
                    syncTrackedShowsFromCloud()
                }
            }
        }
    }

    // Auth methods
    fun getAuthManager() = authManager

    fun signOut() {
        authManager.signOut()
        // Clear local data
        viewModelScope.launch {
            repository.saveTracked(emptyList())
        }
    }

    // Search methods
    fun performSearch() {
        val query = searchText.trim()
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            runCatching { repository.search(query) }
                .onSuccess { _searchResults.value = it }
                .onFailure { _searchError.value = it.message }
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        searchText = ""
        _searchResults.value = emptyList()
        _searchError.value = null
    }

    // Tracked shows methods
    fun toggleTracked(show: Show) {
        viewModelScope.launch {
            val newList = trackedShows.value.toMutableList().apply {
                if (any { it.id == show.id }) removeAll { it.id == show.id } else add(show)
            }
            repository.saveTracked(newList)
            
            // Sync to cloud if signed in
            if (isSignedIn.value) {
                firestoreManager.syncTrackedShows(newList)
            }
        }
    }

    fun isTracked(show: Show) = trackedShows.value.any { it.id == show.id }

    private suspend fun syncTrackedShowsFromCloud() {
        val result = firestoreManager.getTrackedShows()
        result.onSuccess { showIds ->
            // TODO: Fetch show details for these IDs and merge with local
            // For now, just log success
        }
    }

    // Updates feed methods
    fun refreshUpdates() {
        viewModelScope.launch {
            _isLoadingUpdates.value = true
            _updates.value = repository.fetchUpdates(trackedShows.value)
            _isLoadingUpdates.value = false
        }
    }

    // Show detail methods
    fun selectShow(show: Show) {
        _selectedShow.value = show
        loadShowDetails(show)
        loadReviews(show.id)
    }

    fun clearSelection() {
        _selectedShow.value = null
        _reviews.value = emptyList()
        _reviewSummary.value = null
    }

    private fun loadShowDetails(show: Show) {
        viewModelScope.launch {
            val details = runCatching { repository.fetchDetails(show) }.getOrNull()
            details?.let { _selectedShow.value = it }
        }
    }

    fun loadSeasonEpisodes(showId: String, seasonNumber: Int, onLoaded: (List<com.keepup.android.data.EpisodeInfo>) -> Unit) {
        viewModelScope.launch {
            val episodes = repository.fetchSeasonDetails(showId, seasonNumber)
            onLoaded(episodes)
        }
    }

    // Reviews methods
    private fun loadReviews(showId: String) {
        viewModelScope.launch {
            // Load review summary
            firestoreManager.getReviewSummary(showId).onSuccess { summary ->
                _reviewSummary.value = summary
            }

            // Observe reviews in real-time
            firestoreManager.getReviews(showId).collect { reviews ->
                _reviews.value = reviews
            }
        }
    }

    fun submitReview(review: Review, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            firestoreManager.submitReview(review)
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Failed to submit review") }
        }
    }

    fun voteOnReview(reviewId: String, isHelpful: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            firestoreManager.voteOnReview(reviewId, isHelpful)
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Failed to vote") }
        }
    }

    // Watch progress methods
    fun toggleEpisodeWatched(showId: String, seasonNumber: Int, episodeNumber: Int) {
        viewModelScope.launch {
            firestoreManager.saveWatchProgress(showId, seasonNumber, episodeNumber)
        }
    }

    // Navigation methods
    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun focusDiscoverSearch() {
        _shouldFocusDiscoverSearch.value = true
    }

    fun clearDiscoverSearchFocus() {
        _shouldFocusDiscoverSearch.value = false
    }

    // Bug reporting
    fun submitBugReport(title: String, description: String, deviceInfo: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            firestoreManager.submitBugReport(title, description, deviceInfo)
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Failed to submit bug report") }
        }
    }
}
