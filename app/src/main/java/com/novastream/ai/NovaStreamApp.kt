package com.novastream.ai

import android.app.Application
import com.novastream.ai.data.db.NovaDatabaseHelper
import com.novastream.ai.data.repository.NovaRepository

class NovaStreamApp : Application() {

    lateinit var databaseHelper: NovaDatabaseHelper
        private set

    lateinit var repository: NovaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        databaseHelper = NovaDatabaseHelper(this)
        repository = NovaRepository(this, databaseHelper)
        repository.initializeDefaultDataIfNeeded()
    }

    companion object {
        lateinit var instance: NovaStreamApp
            private set
    }
}
