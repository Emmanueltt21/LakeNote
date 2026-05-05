package com.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notes.data.local.dao.NoteDao
import com.notes.data.local.entity.NoteEntity

/**
 * Room Database — the single source of truth for local persistence.
 *
 * KMP-specific considerations:
 * - Room KMP (2.7+) supports both Android and iOS via SQLite driver
 * - On Android: uses the standard Room SQLite driver
 * - On iOS: uses SQLite bundled with the OS via a KMP driver adapter
 * - Database construction is platform-specific (see DatabaseFactory in each platform source set)
 * - exportSchema = true: room will generate JSON schema files for migration testing
 *
 * Migration strategy:
 * - Version bumps require Migration objects or fallbackToDestructiveMigration() for dev
 * - In production: always write explicit Migration(oldVersion, newVersion) {}
 */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}
