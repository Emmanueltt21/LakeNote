package com.notes

import android.app.Application
import com.notes.di.androidModule
import com.notes.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application entry point — initializes Koin DI graph on startup.
 *
 * startKoin {} is called once here and the DI graph is available app-wide.
 * The ordering matters: sharedModules first (infrastructure), then androidModule
 * which overrides the expect DatabaseFactory with the Android actual.
 */
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NotesApplication)
            modules(sharedModules + androidModule)
        }
    }
}
