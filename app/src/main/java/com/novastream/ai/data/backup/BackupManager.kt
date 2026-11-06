package com.novastream.ai.data.backup

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.novastream.ai.data.model.BackupData

object BackupManager {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    fun exportBackup(backupData: BackupData): String {
        return gson.toJson(backupData)
    }

    fun parseBackup(json: String): BackupData {
        val data = gson.fromJson(json, BackupData::class.java)
            ?: throw IllegalArgumentException("Invalid backup JSON format")
        if (data.version <= 0) {
            throw IllegalArgumentException("Unsupported backup version")
        }
        return data
    }
}
