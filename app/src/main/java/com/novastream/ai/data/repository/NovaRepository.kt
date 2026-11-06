package com.novastream.ai.data.repository

import android.content.Context
import com.novastream.ai.data.backup.BackupManager
import com.novastream.ai.data.db.NovaDatabaseHelper
import com.novastream.ai.data.diagnostics.StreamHealthTester
import com.novastream.ai.data.model.*
import com.novastream.ai.data.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NovaRepository(
    private val context: Context,
    private val dbHelper: NovaDatabaseHelper
) {

    fun initializeDefaultDataIfNeeded() {
        val users = dbHelper.getAllUsers()
        if (users.isEmpty()) {
            val defaultUser = UserProfile(
                name = "Nova Admin",
                avatar = "admin_shield",
                pinCode = null,
                isKidsMode = false
            )
            val userId = dbHelper.insertUser(defaultUser)

            // Seed sample high quality public legal streams for immediate verification
            val samplePlaylist = Playlist(
                title = "Nova Global Demo Streams",
                sourceType = "BUILTIN",
                sourceValue = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                channelCount = 6,
                liveCount = 4,
                vodCount = 2,
                seriesCount = 0
            )
            val plId = dbHelper.insertPlaylist(samplePlaylist)

            val initialChannels = listOf(
                Channel(
                    playlistId = plId,
                    name = "Big Buck Bunny (HLS Live Test)",
                    streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                    category = "Animation / Test",
                    logoUrl = "https://upload.wikimedia.org/wikipedia/commons/c/c5/Big_buck_bunny_poster_big.jpg",
                    contentType = ContentType.LIVE,
                    isFavorite = true,
                    isWorking = true,
                    latencyMs = 120
                ),
                Channel(
                    playlistId = plId,
                    name = "Tears of Steel (4K VOD)",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    category = "Sci-Fi / Movies",
                    logoUrl = "https://upload.wikimedia.org/wikipedia/commons/7/70/Tears_of_Steel_poster.jpg",
                    contentType = ContentType.VOD,
                    isFavorite = true,
                    isWorking = true,
                    latencyMs = 95
                ),
                Channel(
                    playlistId = plId,
                    name = "Sintel Movie (1080p HLS)",
                    streamUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
                    category = "Fantasy / Movies",
                    logoUrl = "https://upload.wikimedia.org/wikipedia/commons/4/4b/Sintel_poster.jpg",
                    contentType = ContentType.VOD,
                    isFavorite = false,
                    isWorking = true,
                    latencyMs = 140
                ),
                Channel(
                    playlistId = plId,
                    name = "Elephants Dream (HLS Test)",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    category = "Animation / Test",
                    logoUrl = "https://upload.wikimedia.org/wikipedia/commons/0/0c/Elephants_Dream_poster.jpg",
                    contentType = ContentType.LIVE,
                    isFavorite = false,
                    isWorking = true,
                    latencyMs = 110
                ),
                Channel(
                    playlistId = plId,
                    name = "For Bigger Blazes (HD Promo)",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    category = "Promo / Action",
                    logoUrl = null,
                    contentType = ContentType.LIVE,
                    isFavorite = false,
                    isWorking = true,
                    latencyMs = 85
                ),
                Channel(
                    playlistId = plId,
                    name = "We Are Going On Bullrun (Action)",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    category = "Automotive / Sports",
                    logoUrl = null,
                    contentType = ContentType.LIVE,
                    isFavorite = false,
                    isWorking = true,
                    latencyMs = 130
                )
            )

            dbHelper.insertChannelsBatch(initialChannels, plId)
            dbHelper.logActivity("INITIALIZE", "Initialized Nova Stream AI with sample demo streams and admin profile.")
        }
    }

    suspend fun fetchUrlContent(urlStr: String): String = withContext(Dispatchers.IO) {
        val url = URL(urlStr)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 15000
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "NovaStreamAI/1.0 (Android M3U)")
        }

        conn.connect()
        if (conn.responseCode !in 200..299) {
            throw IllegalArgumentException("HTTP error ${conn.responseCode}: ${conn.responseMessage}")
        }

        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val sb = StringBuilder()
        var line = reader.readLine()
        while (line != null) {
            sb.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        conn.disconnect()
        sb.toString()
    }

    suspend fun importPlaylistFromUrl(title: String, url: String): Playlist = withContext(Dispatchers.IO) {
        val content = fetchUrlContent(url)
        importPlaylistFromContent(title, "URL", url, content)
    }

    suspend fun importPlaylistFromContent(
        title: String,
        sourceType: String,
        sourceValue: String,
        content: String
    ): Playlist = withContext(Dispatchers.IO) {
        val parsedChannels = M3UParser.parse(content)
        if (parsedChannels.isEmpty()) {
            throw IllegalArgumentException("No valid stream channels found in playlist content.")
        }

        val liveCount = parsedChannels.count { it.contentType == ContentType.LIVE }
        val vodCount = parsedChannels.count { it.contentType == ContentType.VOD }
        val seriesCount = parsedChannels.count { it.contentType == ContentType.SERIES }

        val playlist = Playlist(
            title = title.ifBlank { "Imported Playlist" },
            sourceType = sourceType,
            sourceValue = sourceValue,
            channelCount = parsedChannels.size,
            liveCount = liveCount,
            vodCount = vodCount,
            seriesCount = seriesCount,
            lastUpdated = System.currentTimeMillis()
        )

        val plId = dbHelper.insertPlaylist(playlist)
        dbHelper.insertChannelsBatch(parsedChannels, plId)
        dbHelper.logActivity("IMPORT", "Imported '${playlist.title}' with ${parsedChannels.size} channels.")

        playlist.copy(id = plId)
    }

    suspend fun getAllPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        dbHelper.getAllPlaylists()
    }

    suspend fun deletePlaylist(id: Long) = withContext(Dispatchers.IO) {
        dbHelper.deletePlaylist(id)
        dbHelper.logActivity("DELETE", "Removed playlist ID: $id")
    }

    suspend fun getAllChannels(): List<Channel> = withContext(Dispatchers.IO) {
        dbHelper.getAllChannels()
    }

    suspend fun getChannelsByPlaylist(playlistId: Long): List<Channel> = withContext(Dispatchers.IO) {
        dbHelper.getChannelsByPlaylist(playlistId)
    }

    suspend fun getFavoriteChannels(): List<Channel> = withContext(Dispatchers.IO) {
        dbHelper.getFavoriteChannels()
    }

    suspend fun toggleFavorite(channelId: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        dbHelper.toggleFavorite(channelId, isFav)
    }

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        dbHelper.getCategories()
    }

    suspend fun recordViewingHistory(channel: Channel, durationSec: Long) = withContext(Dispatchers.IO) {
        dbHelper.recordHistory(channel, durationSec)
    }

    suspend fun getRecentHistory(): List<ViewingHistory> = withContext(Dispatchers.IO) {
        dbHelper.getRecentHistory()
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dbHelper.clearHistory()
        dbHelper.logActivity("CLEAR_HISTORY", "Cleared viewing history.")
    }

    suspend fun getActivityLogs(): List<ActivityLog> = withContext(Dispatchers.IO) {
        dbHelper.getRecentLogs()
    }

    suspend fun getAllUsers(): List<UserProfile> = withContext(Dispatchers.IO) {
        dbHelper.getAllUsers()
    }

    suspend fun createUser(name: String, pin: String?, isKids: Boolean): UserProfile = withContext(Dispatchers.IO) {
        val user = UserProfile(name = name, pinCode = pin, isKidsMode = isKids)
        val id = dbHelper.insertUser(user)
        dbHelper.logActivity("USER_CREATED", "Created user profile '$name'.")
        user.copy(id = id)
    }

    suspend fun deleteUser(id: Long) = withContext(Dispatchers.IO) {
        dbHelper.deleteUser(id)
    }

    suspend fun testStreamHealth(url: String): StreamHealth = withContext(Dispatchers.IO) {
        val health = StreamHealthTester.testStream(url)
        dbHelper.logActivity("DIAGNOSTICS", "Tested ${url.take(30)}... status: ${health.httpStatus} (${health.responseTimeMs}ms)")
        health
    }

    suspend fun getSettings(): ApplicationSettings = withContext(Dispatchers.IO) {
        val themeStr = dbHelper.getSetting("theme_mode", ThemeMode.DARK_GRAPHITE.name)
        val lang = dbHelper.getSetting("language", "en")
        val timeout = dbHelper.getSetting("timeout", "10").toIntOrNull() ?: 10
        val buffer = dbHelper.getSetting("buffer", "BALANCED")
        val hw = dbHelper.getSetting("hw_accel", "true").toBoolean()
        val userId = dbHelper.getSetting("active_user", "1").toLongOrNull() ?: 1L
        val pin = dbHelper.getSetting("parental_pin", "")

        ApplicationSettings(
            themeMode = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.DARK_GRAPHITE },
            language = lang,
            streamTimeoutSeconds = timeout,
            bufferPreset = buffer,
            hwAcceleration = hw,
            activeUserId = userId,
            parentalPin = pin.ifBlank { null }
        )
    }

    suspend fun saveSettings(settings: ApplicationSettings) = withContext(Dispatchers.IO) {
        dbHelper.setSetting("theme_mode", settings.themeMode.name)
        dbHelper.setSetting("language", settings.language)
        dbHelper.setSetting("timeout", settings.streamTimeoutSeconds.toString())
        dbHelper.setSetting("buffer", settings.bufferPreset)
        dbHelper.setSetting("hw_accel", settings.hwAcceleration.toString())
        dbHelper.setSetting("active_user", settings.activeUserId.toString())
        dbHelper.setSetting("parental_pin", settings.parentalPin ?: "")
        dbHelper.logActivity("SETTINGS", "Application preferences updated.")
    }

    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val backup = BackupData(
            profiles = dbHelper.getAllUsers(),
            playlists = dbHelper.getAllPlaylists(),
            channels = dbHelper.getAllChannels(),
            settings = getSettings()
        )
        BackupManager.exportBackup(backup)
    }

    suspend fun restoreBackupJson(json: String) = withContext(Dispatchers.IO) {
        val backup = BackupManager.parseBackup(json)
        dbHelper.resetAllData()

        for (u in backup.profiles) {
            dbHelper.insertUser(u)
        }
        for (pl in backup.playlists) {
            val plId = dbHelper.insertPlaylist(pl)
            val channelsForPl = backup.channels.filter { it.playlistId == pl.id }
            dbHelper.insertChannelsBatch(channelsForPl, plId)
        }
        saveSettings(backup.settings)
        dbHelper.logActivity("RESTORE", "Restored backup with ${backup.playlists.size} playlists and ${backup.channels.size} channels.")
    }

    suspend fun resetDatabase() = withContext(Dispatchers.IO) {
        dbHelper.resetAllData()
        initializeDefaultDataIfNeeded()
    }
}
