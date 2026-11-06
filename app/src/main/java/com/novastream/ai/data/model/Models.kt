package com.novastream.ai.data.model

enum class ContentType {
    LIVE,
    VOD,
    SERIES,
    RADIO
}

enum class ThemeMode {
    DARK_GRAPHITE,
    CYBER_VIOLET,
    PURE_BLACK,
    LIGHT
}

enum class Screen(val title: String) {
    DASHBOARD("Dashboard"),
    PLAYLISTS("Playlists"),
    CHANNELS("Channels"),
    PLAYER("Media Player"),
    DIAGNOSTICS("Diagnostics"),
    ANALYTICS("Analytics"),
    PROFILES("Profiles"),
    SETTINGS("Settings")
}

data class UserProfile(
    val id: Long = 0,
    val name: String,
    val avatar: String = "default_avatar",
    val pinCode: String? = null,
    val isKidsMode: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Playlist(
    val id: Long = 0,
    val title: String,
    val sourceType: String, // "URL", "FILE", "COMBO"
    val sourceValue: String,
    val channelCount: Int = 0,
    val liveCount: Int = 0,
    val vodCount: Int = 0,
    val seriesCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class Channel(
    val id: Long = 0,
    val playlistId: Long,
    val name: String,
    val streamUrl: String,
    val category: String = "General",
    val logoUrl: String? = null,
    val epgId: String? = null,
    val contentType: ContentType = ContentType.LIVE,
    val isFavorite: Boolean = false,
    val isWorking: Boolean = true,
    val latencyMs: Long = 0,
    val lastChecked: Long = 0
)

data class Category(
    val id: Long = 0,
    val name: String,
    val contentType: ContentType = ContentType.LIVE,
    val channelCount: Int = 0
)

data class ViewingHistory(
    val id: Long = 0,
    val channelId: Long,
    val channelName: String,
    val category: String,
    val streamUrl: String,
    val watchedDurationSeconds: Long,
    val lastWatchedAt: Long = System.currentTimeMillis()
)

data class ApplicationSettings(
    val themeMode: ThemeMode = ThemeMode.DARK_GRAPHITE,
    val language: String = "en",
    val streamTimeoutSeconds: Int = 10,
    val bufferPreset: String = "BALANCED", // LOW_LATENCY, BALANCED, HIGH_BUFFER
    val hwAcceleration: Boolean = true,
    val activeUserId: Long = 1,
    val parentalPin: String? = null
)

data class StreamHealth(
    val url: String,
    val isReachable: Boolean,
    val httpStatus: Int,
    val responseTimeMs: Long,
    val contentType: String? = null,
    val serverHeader: String? = null,
    val errorDetails: String? = null
)

data class ActivityLog(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String,
    val description: String
)

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val profiles: List<UserProfile>,
    val playlists: List<Playlist>,
    val channels: List<Channel>,
    val settings: ApplicationSettings
)
