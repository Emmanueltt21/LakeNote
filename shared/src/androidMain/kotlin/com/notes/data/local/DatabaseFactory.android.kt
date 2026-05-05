package com.notes.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android-specific database creation.
 *
 * Receives [Context] via Koin injection — see androidModule in di/AppModule.kt
 * Room needs Context to locate the app's database directory.
 *
 * Production additions to consider:
 * - .addMigrations(MIGRATION_1_2) — structured schema migrations
 * - .enableMultiInstanceInvalidation() — for multi-process apps
 * - .setQueryCoroutineContext(Dispatchers.IO) — explicit IO dispatcher
 */
actual class DatabaseFactory(private val context: Context) {
    actual fun create(): NotesDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            klass = NotesDatabase::class.java,
            name = NotesDatabase.DATABASE_NAME,
        )
        .fallbackToDestructiveMigration(dropAllTables = true) // Dev only — use migrations in prod
        .build()
    }
}
