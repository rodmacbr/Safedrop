package org.wit.safedrop.main

import android.app.Application
import org.wit.safedrop.models.SafedropJSONStore
import org.wit.safedrop.models.SafedropStore
import timber.log.Timber
import timber.log.Timber.Forest.i

class MainApp : Application() {

    lateinit var safedrops: SafedropStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        safedrops = SafedropJSONStore(applicationContext)
        i("Safedrop started")
    }
}