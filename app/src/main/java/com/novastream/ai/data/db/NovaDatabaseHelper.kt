package com.novastream.ai.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.novastream.ai.data.model.*

class NovaDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "novastream.db"
        const val DATABASE_VERSION = 1

        // Table Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_AVATAR = "avatar"
        const val COL_USER_PIN = "pin_code"
        const val COL_USER_IS_KIDS = "is_kids"
        const val COL_USER_CREATED = "created_at"

        // Table Playlists
        const val TABLE_PLAYLISTS = "playlists"
        const val COL_PL_ID = "id"
        const val COL_PL_TITLE = "title"
        const val COL_PL_SOURCE_TYPE = "source_type"
        const val COL_PL_SOURCE_VAL = "source_val"
        const val COL_PL_CHANNEL_CNT = "channel_cnt"
        const val COL_PL_LIVE_CNT = "live_cnt"
        const val COL_PL_VOD_CNT = "vod_cnt"
        const val COL_PL_SERIES_CNT = "series_cnt"
        const val COL_PL_UPDATED_AT = "updated_at"
        const val COL_PL_IS_ACTIVE = "is_active"

        // Table Channels
        const val TABLE_CHANNELS = "channels"
        const val COL_CH_ID = "id"
        const val COL_CH_PL_ID = "playlist_id"
        const val COL_CH_NAME = "name"
        const val COL_CH_URL = "stream_url"
        const val COL_CH_CATEGORY = "category"
        const val COL_CH_LOGO = "logo_url"
        const val COL_CH_EPG = "epg_id"
        const val COL_CH_TYPE = "content_type"
        const val COL_CH_FAVORITE = "is_favorite"
        const val COL_CH_WORKING = "is_working"
        const val COL_CH_LATENCY = "latency_ms"
        const val COL_CH_LAST_CHECKED = "last_checked"

        // Table Viewing History
        const val TABLE_HISTORY = "viewing_history"
        const val COL_HIST_ID = "id"
        const val COL_HIST_CH_ID = "channel_id"
        const val COL_HIST_CH_NAME = "channel_name"
        const val COL_HIST_CAT = "category"
        const val COL_HIST_URL = "stream_url"
        const val COL_HIST_DURATION = "duration"
        const val COL_HIST_WATCHED_AT = "watched_at"

        // Table Activity Logs
        const val TABLE_LOGS = "activity_logs"
        const val COL_LOG_ID = "id"
        const val COL_LOG_TIME = "timestamp"
        const val COL_LOG_ACTION = "action_type"
        const val COL_LOG_DESC = "description"

        // Table Settings
        const val TABLE_SETTINGS = "settings"
        const val COL_SET_KEY = "set_key"
        const val COL_SET_VAL = "set_val"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL,
                $COL_USER_AVATAR TEXT,
                $COL_USER_PIN TEXT,
                $COL_USER_IS_KIDS INTEGER DEFAULT 0,
                $COL_USER_CREATED INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PLAYLISTS (
                $COL_PL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PL_TITLE TEXT NOT NULL,
                $COL_PL_SOURCE_TYPE TEXT,
                $COL_PL_SOURCE_VAL TEXT,
                $COL_PL_CHANNEL_CNT INTEGER DEFAULT 0,
                $COL_PL_LIVE_CNT INTEGER DEFAULT 0,
                $COL_PL_VOD_CNT INTEGER DEFAULT 0,
                $COL_PL_SERIES_CNT INTEGER DEFAULT 0,
                $COL_PL_UPDATED_AT INTEGER,
                $COL_PL_IS_ACTIVE INTEGER DEFAULT 1
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CHANNELS (
                $COL_CH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CH_PL_ID INTEGER,
                $COL_CH_NAME TEXT NOT NULL,
                $COL_CH_URL TEXT NOT NULL,
                $COL_CH_CATEGORY TEXT,
                $COL_CH_LOGO TEXT,
                $COL_CH_EPG TEXT,
                $COL_CH_TYPE TEXT,
                $COL_CH_FAVORITE INTEGER DEFAULT 0,
                $COL_CH_WORKING INTEGER DEFAULT 1,
                $COL_CH_LATENCY INTEGER DEFAULT 0,
                $COL_CH_LAST_CHECKED INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_HISTORY (
                $COL_HIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HIST_CH_ID INTEGER,
                $COL_HIST_CH_NAME TEXT,
                $COL_HIST_CAT TEXT,
                $COL_HIST_URL TEXT,
                $COL_HIST_DURATION INTEGER DEFAULT 0,
                $COL_HIST_WATCHED_AT INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_LOGS (
                $COL_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LOG_TIME INTEGER,
                $COL_LOG_ACTION TEXT,
                $COL_LOG_DESC TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_SETTINGS (
                $COL_SET_KEY TEXT PRIMARY KEY,
                $COL_SET_VAL TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHANNELS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    // ==========================================
    // USER PROFILES
    // ==========================================
    fun insertUser(user: UserProfile): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_USER_NAME, user.name)
            put(COL_USER_AVATAR, user.avatar)
            put(COL_USER_PIN, user.pinCode)
            put(COL_USER_IS_KIDS, if (user.isKidsMode) 1 else 0)
            put(COL_USER_CREATED, user.createdAt)
        }
        return db.insert(TABLE_USERS, null, cv)
    }

    fun getAllUsers(): List<UserProfile> {
        val list = mutableListOf<UserProfile>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS ORDER BY $COL_USER_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    UserProfile(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_USER_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_USER_NAME)),
                        avatar = it.getString(it.getColumnIndexOrThrow(COL_USER_AVATAR)),
                        pinCode = it.getString(it.getColumnIndexOrThrow(COL_USER_PIN)),
                        isKidsMode = it.getInt(it.getColumnIndexOrThrow(COL_USER_IS_KIDS)) == 1,
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_USER_CREATED))
                    )
                )
            }
        }
        return list
    }

    fun deleteUser(id: Long) {
        writableDatabase.delete(TABLE_USERS, "$COL_USER_ID = ?", arrayOf(id.toString()))
    }

    // ==========================================
    // PLAYLISTS
    // ==========================================
    fun insertPlaylist(playlist: Playlist): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PL_TITLE, playlist.title)
            put(COL_PL_SOURCE_TYPE, playlist.sourceType)
            put(COL_PL_SOURCE_VAL, playlist.sourceValue)
            put(COL_PL_CHANNEL_CNT, playlist.channelCount)
            put(COL_PL_LIVE_CNT, playlist.liveCount)
            put(COL_PL_VOD_CNT, playlist.vodCount)
            put(COL_PL_SERIES_CNT, playlist.seriesCount)
            put(COL_PL_UPDATED_AT, playlist.lastUpdated)
            put(COL_PL_IS_ACTIVE, if (playlist.isActive) 1 else 0)
        }
        return db.insert(TABLE_PLAYLISTS, null, cv)
    }

    fun updatePlaylistCounts(playlistId: Long, total: Int, live: Int, vod: Int, series: Int) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PL_CHANNEL_CNT, total)
            put(COL_PL_LIVE_CNT, live)
            put(COL_PL_VOD_CNT, vod)
            put(COL_PL_SERIES_CNT, series)
            put(COL_PL_UPDATED_AT, System.currentTimeMillis())
        }
        db.update(TABLE_PLAYLISTS, cv, "$COL_PL_ID = ?", arrayOf(playlistId.toString()))
    }

    fun getAllPlaylists(): List<Playlist> {
        val list = mutableListOf<Playlist>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PLAYLISTS ORDER BY $COL_PL_UPDATED_AT DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Playlist(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_PL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_PL_TITLE)),
                        sourceType = it.getString(it.getColumnIndexOrThrow(COL_PL_SOURCE_TYPE)),
                        sourceValue = it.getString(it.getColumnIndexOrThrow(COL_PL_SOURCE_VAL)),
                        channelCount = it.getInt(it.getColumnIndexOrThrow(COL_PL_CHANNEL_CNT)),
                        liveCount = it.getInt(it.getColumnIndexOrThrow(COL_PL_LIVE_CNT)),
                        vodCount = it.getInt(it.getColumnIndexOrThrow(COL_PL_VOD_CNT)),
                        seriesCount = it.getInt(it.getColumnIndexOrThrow(COL_PL_SERIES_CNT)),
                        lastUpdated = it.getLong(it.getColumnIndexOrThrow(COL_PL_UPDATED_AT)),
                        isActive = it.getInt(it.getColumnIndexOrThrow(COL_PL_IS_ACTIVE)) == 1
                    )
                )
            }
        }
        return list
    }

    fun deletePlaylist(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_CHANNELS, "$COL_CH_PL_ID = ?", arrayOf(id.toString()))
        db.delete(TABLE_PLAYLISTS, "$COL_PL_ID = ?", arrayOf(id.toString()))
    }

    // ==========================================
    // CHANNELS
    // ==========================================
    fun insertChannelsBatch(channels: List<Channel>, playlistId: Long) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (ch in channels) {
                val cv = ContentValues().apply {
                    put(COL_CH_PL_ID, playlistId)
                    put(COL_CH_NAME, ch.name)
                    put(COL_CH_URL, ch.streamUrl)
                    put(COL_CH_CATEGORY, ch.category)
                    put(COL_CH_LOGO, ch.logoUrl)
                    put(COL_CH_EPG, ch.epgId)
                    put(COL_CH_TYPE, ch.contentType.name)
                    put(COL_CH_FAVORITE, if (ch.isFavorite) 1 else 0)
                    put(COL_CH_WORKING, if (ch.isWorking) 1 else 0)
                    put(COL_CH_LATENCY, ch.latencyMs)
                    put(COL_CH_LAST_CHECKED, ch.lastChecked)
                }
                db.insert(TABLE_CHANNELS, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllChannels(): List<Channel> {
        val list = mutableListOf<Channel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CHANNELS ORDER BY $COL_CH_NAME ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToChannel(it))
            }
        }
        return list
    }

    fun getChannelsByPlaylist(playlistId: Long): List<Channel> {
        val list = mutableListOf<Channel>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CHANNELS WHERE $COL_CH_PL_ID = ? ORDER BY $COL_CH_NAME ASC",
            arrayOf(playlistId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToChannel(it))
            }
        }
        return list
    }

    fun getFavoriteChannels(): List<Channel> {
        val list = mutableListOf<Channel>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CHANNELS WHERE $COL_CH_FAVORITE = 1 ORDER BY $COL_CH_NAME ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToChannel(it))
            }
        }
        return list
    }

    fun toggleFavorite(channelId: Long, isFavorite: Boolean) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CH_FAVORITE, if (isFavorite) 1 else 0)
        }
        db.update(TABLE_CHANNELS, cv, "$COL_CH_ID = ?", arrayOf(channelId.toString()))
    }

    fun updateChannelHealth(channelId: Long, isWorking: Boolean, latencyMs: Long) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CH_WORKING, if (isWorking) 1 else 0)
            put(COL_CH_LATENCY, latencyMs)
            put(COL_CH_LAST_CHECKED, System.currentTimeMillis())
        }
        db.update(TABLE_CHANNELS, cv, "$COL_CH_ID = ?", arrayOf(channelId.toString()))
    }

    fun getCategories(): List<Category> {
        val list = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_CH_CATEGORY, $COL_CH_TYPE, COUNT(*) as cnt FROM $TABLE_CHANNELS GROUP BY $COL_CH_CATEGORY, $COL_CH_TYPE ORDER BY cnt DESC",
            null
        )
        var idCounter = 1L
        cursor.use {
            while (it.moveToNext()) {
                val cat = it.getString(0) ?: "General"
                val typeStr = it.getString(1) ?: "LIVE"
                val count = it.getInt(2)
                val type = try {
                    ContentType.valueOf(typeStr)
                } catch (e: Exception) {
                    ContentType.LIVE
                }
                list.add(Category(id = idCounter++, name = cat, contentType = type, channelCount = count))
            }
        }
        return list
    }

    private fun cursorToChannel(c: Cursor): Channel {
        val typeStr = c.getString(c.getColumnIndexOrThrow(COL_CH_TYPE)) ?: "LIVE"
        val contentType = try {
            ContentType.valueOf(typeStr)
        } catch (e: Exception) {
            ContentType.LIVE
        }
        return Channel(
            id = c.getLong(c.getColumnIndexOrThrow(COL_CH_ID)),
            playlistId = c.getLong(c.getColumnIndexOrThrow(COL_CH_PL_ID)),
            name = c.getString(c.getColumnIndexOrThrow(COL_CH_NAME)),
            streamUrl = c.getString(c.getColumnIndexOrThrow(COL_CH_URL)),
            category = c.getString(c.getColumnIndexOrThrow(COL_CH_CATEGORY)) ?: "General",
            logoUrl = c.getString(c.getColumnIndexOrThrow(COL_CH_LOGO)),
            epgId = c.getString(c.getColumnIndexOrThrow(COL_CH_EPG)),
            contentType = contentType,
            isFavorite = c.getInt(c.getColumnIndexOrThrow(COL_CH_FAVORITE)) == 1,
            isWorking = c.getInt(c.getColumnIndexOrThrow(COL_CH_WORKING)) == 1,
            latencyMs = c.getLong(c.getColumnIndexOrThrow(COL_CH_LATENCY)),
            lastChecked = c.getLong(c.getColumnIndexOrThrow(COL_CH_LAST_CHECKED))
        )
    }

    // ==========================================
    // VIEWING HISTORY
    // ==========================================
    fun recordHistory(channel: Channel, durationSecs: Long) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_HIST_CH_ID, channel.id)
            put(COL_HIST_CH_NAME, channel.name)
            put(COL_HIST_CAT, channel.category)
            put(COL_HIST_URL, channel.streamUrl)
            put(COL_HIST_DURATION, durationSecs)
            put(COL_HIST_WATCHED_AT, System.currentTimeMillis())
        }
        db.insert(TABLE_HISTORY, null, cv)
    }

    fun getRecentHistory(limit: Int = 50): List<ViewingHistory> {
        val list = mutableListOf<ViewingHistory>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_HISTORY ORDER BY $COL_HIST_WATCHED_AT DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ViewingHistory(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_HIST_ID)),
                        channelId = it.getLong(it.getColumnIndexOrThrow(COL_HIST_CH_ID)),
                        channelName = it.getString(it.getColumnIndexOrThrow(COL_HIST_CH_NAME)),
                        category = it.getString(it.getColumnIndexOrThrow(COL_HIST_CAT)) ?: "",
                        streamUrl = it.getString(it.getColumnIndexOrThrow(COL_HIST_URL)),
                        watchedDurationSeconds = it.getLong(it.getColumnIndexOrThrow(COL_HIST_DURATION)),
                        lastWatchedAt = it.getLong(it.getColumnIndexOrThrow(COL_HIST_WATCHED_AT))
                    )
                )
            }
        }
        return list
    }

    fun clearHistory() {
        writableDatabase.delete(TABLE_HISTORY, null, null)
    }

    // ==========================================
    // ACTIVITY LOGS
    // ==========================================
    fun logActivity(actionType: String, description: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_LOG_TIME, System.currentTimeMillis())
            put(COL_LOG_ACTION, actionType)
            put(COL_LOG_DESC, description)
        }
        db.insert(TABLE_LOGS, null, cv)
    }

    fun getRecentLogs(limit: Int = 30): List<ActivityLog> {
        val list = mutableListOf<ActivityLog>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_LOGS ORDER BY $COL_LOG_TIME DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ActivityLog(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_LOG_ID)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_LOG_TIME)),
                        actionType = it.getString(it.getColumnIndexOrThrow(COL_LOG_ACTION)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_LOG_DESC))
                    )
                )
            }
        }
        return list
    }

    // ==========================================
    // SETTINGS
    // ==========================================
    fun setSetting(key: String, value: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_SET_KEY, key)
            put(COL_SET_VAL, value)
        }
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getSetting(key: String, default: String): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_SET_VAL FROM $TABLE_SETTINGS WHERE $COL_SET_KEY = ?", arrayOf(key))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0) ?: default
            }
        }
        return default
    }

    fun resetAllData() {
        val db = writableDatabase
        db.delete(TABLE_CHANNELS, null, null)
        db.delete(TABLE_PLAYLISTS, null, null)
        db.delete(TABLE_HISTORY, null, null)
        db.delete(TABLE_LOGS, null, null)
        db.delete(TABLE_USERS, null, null)
        db.delete(TABLE_SETTINGS, null, null)
    }
}
