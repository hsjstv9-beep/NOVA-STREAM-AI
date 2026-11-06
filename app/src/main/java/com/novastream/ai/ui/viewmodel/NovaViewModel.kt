package com.novastream.ai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novastream.ai.data.model.*
import com.novastream.ai.data.parser.M3UParser
import com.novastream.ai.data.repository.NovaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NovaViewModel(
    private val repository: NovaRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _profiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val profiles: StateFlow<List<UserProfile>> = _profiles.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _playingChannel = MutableStateFlow<Channel?>(null)
    val playingChannel: StateFlow<Channel?> = _playingChannel.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _favorites = MutableStateFlow<List<Channel>>(emptyList())
    val favorites: StateFlow<List<Channel>> = _favorites.asStateFlow()

    private val _history = MutableStateFlow<List<ViewingHistory>>(emptyList())
    val history: StateFlow<List<ViewingHistory>> = _history.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _settings = MutableStateFlow(ApplicationSettings())
    val settings: StateFlow<ApplicationSettings> = _settings.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("ALL")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    // Diagnostics
    private val _diagnosticsResult = MutableStateFlow<StreamHealth?>(null)
    val diagnosticsResult: StateFlow<StreamHealth?> = _diagnosticsResult.asStateFlow()

    private val _isTestingDiagnostics = MutableStateFlow(false)
    val isTestingDiagnostics: StateFlow<Boolean> = _isTestingDiagnostics.asStateFlow()

    // Import status
    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    // Combolist Tool
    private val _fixedCombos = MutableStateFlow<List<String>>(emptyList())
    val fixedCombos: StateFlow<List<String>> = _fixedCombos.asStateFlow()

    private val _fixedErrorsCount = MutableStateFlow(0)
    val fixedErrorsCount: StateFlow<Int> = _fixedErrorsCount.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _profiles.value = repository.getAllUsers()
            if (_currentUser.value == null && _profiles.value.isNotEmpty()) {
                _currentUser.value = _profiles.value.first()
            }
            _playlists.value = repository.getAllPlaylists()
            _channels.value = repository.getAllChannels()
            _categories.value = repository.getCategories()
            _favorites.value = repository.getFavoriteChannels()
            _history.value = repository.getRecentHistory()
            _activityLogs.value = repository.getActivityLogs()
            _settings.value = repository.getSettings()

            if (_playingChannel.value == null && _channels.value.isNotEmpty()) {
                _playingChannel.value = _channels.value.first()
            }
        }
    }

    fun switchScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    fun playChannel(channel: Channel) {
        _playingChannel.value = channel
        _currentScreen.value = Screen.PLAYER
        viewModelScope.launch {
            repository.recordViewingHistory(channel, 0)
            _history.value = repository.getRecentHistory()
        }
    }

    fun playDirectUrl(title: String, url: String) {
        val directChannel = Channel(
            id = 99999,
            playlistId = 0,
            name = title.ifBlank { "Direct Stream" },
            streamUrl = url,
            category = "Direct",
            contentType = ContentType.LIVE
        )
        _playingChannel.value = directChannel
        _currentScreen.value = Screen.PLAYER
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            val newFav = !channel.isFavorite
            repository.toggleFavorite(channel.id, newFav)
            _channels.value = repository.getAllChannels()
            _favorites.value = repository.getFavoriteChannels()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun importPlaylist(title: String, urlOrContent: String, isUrl: Boolean) {
        viewModelScope.launch {
            _isImporting.value = true
            _importMessage.value = null
            try {
                if (isUrl) {
                    repository.importPlaylistFromUrl(title, urlOrContent)
                } else {
                    repository.importPlaylistFromContent(title, "TEXT", "Manual Entry", urlOrContent)
                }
                _importMessage.value = "Success! Playlist imported successfully."
                refreshAll()
                _currentScreen.value = Screen.PLAYLISTS
            } catch (e: Exception) {
                _importMessage.value = "Import Error: ${e.message ?: "Failed to import"}"
            } finally {
                _isImporting.value = false
            }
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(id)
            refreshAll()
        }
    }

    fun testStream(url: String) {
        viewModelScope.launch {
            _isTestingDiagnostics.value = true
            _diagnosticsResult.value = null
            try {
                val result = repository.testStreamHealth(url)
                _diagnosticsResult.value = result
            } finally {
                _isTestingDiagnostics.value = false
            }
        }
    }

    fun fixCombolist(rawText: String) {
        val (fixed, errors) = M3UParser.fixCombolist(rawText)
        _fixedCombos.value = fixed
        _fixedErrorsCount.value = errors
    }

    fun switchProfile(user: UserProfile) {
        _currentUser.value = user
        viewModelScope.launch {
            val updated = _settings.value.copy(activeUserId = user.id)
            repository.saveSettings(updated)
            _settings.value = updated
        }
    }

    fun createProfile(name: String, pin: String?, isKids: Boolean) {
        viewModelScope.launch {
            repository.createUser(name, pin, isKids)
            _profiles.value = repository.getAllUsers()
        }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            repository.deleteUser(id)
            _profiles.value = repository.getAllUsers()
            if (_currentUser.value?.id == id) {
                _currentUser.value = _profiles.value.firstOrNull()
            }
        }
    }

    fun updateSettings(settings: ApplicationSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            _settings.value = settings
        }
    }

    suspend fun exportBackupJson(): String {
        return repository.exportBackupJson()
    }

    fun restoreBackup(json: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.restoreBackupJson(json)
                refreshAll()
                onComplete(true, "Backup restored successfully.")
            } catch (e: Exception) {
                onComplete(false, "Restore failed: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun clearViewingHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _history.value = emptyList()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetDatabase()
            refreshAll()
            _currentScreen.value = Screen.DASHBOARD
        }
    }
}
