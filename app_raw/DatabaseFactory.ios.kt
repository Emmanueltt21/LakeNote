package com.notes.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS-specific database creation using the bundled SQLite driver.
 *
 * Key differences from Android:
 * 1. No Context — iOS uses NSFileManager to get the Documents directory
 * 2. BundledSQLiteDriver — KMP bundles its own SQLite for iOS
 *    (Android uses the OS-provided SQLite via the framework driver)
 * 3. setDriver() is required for iOS targets
 *
 * The Documents directory is the correct place for user-generated data on iOS:
 * - Backed up by iCloud automatically
 * - Not cleared when the app is in the background
 */
actual class DatabaseFactory {
    actual fun create(): NotesDatabase {
        val dbPath = documentDirectory() + "/${NotesDatabase.DATABASE_NAME}"
        return Room.databaseBuilder<NotesDatabase>(
            name = dbPath,
        )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}
