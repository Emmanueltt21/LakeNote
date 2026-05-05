package com.notes.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object — Room's contract for database operations.
 *
 * Interview talking points:
 * - @Upsert = INSERT OR REPLACE: handles both create and update in one annotation
 * - All observation queries return Flow<> — Room notifies on every DB change
 * - Suspend functions for one-shot operations, Flow for streams
 * - Raw SQL with ORDER BY columns for flexible sorting without extra indices
 * - FTS (Full-Text Search) uses LIKE here for simplicity;
 *   for production use @Fts4 on a separate FTS table joined to notes
 */
@Dao
interface NoteDao {

    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    @Query("SELECT * FROM notes WHERE is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun observeNotesByUpdated(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_archived = 0 ORDER BY is_pinned DESC, created_at DESC")
    fun observeNotesByCreated(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_archived = 0 ORDER BY is_pinned DESC, priority DESC")
    fun observeNotesByPriority(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_archived = 0 ORDER BY is_pinned DESC, title ASC")
    fun observeNotesByTitle(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_archived = 1 ORDER BY updated_at DESC")
    fun observeArchivedNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE is_archived = 0
          AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY priority DESC, updated_at DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE priority = :priority AND is_archived = 0 ORDER BY updated_at DESC")
    fun observeNotesByPriorityLevel(priority: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("UPDATE notes SET is_archived = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun archiveNote(id: Long, timestamp: Long)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotes(ids: List<Long>)

    @Query("UPDATE notes SET is_pinned = NOT is_pinned, updated_at = :timestamp WHERE id = :id")
    suspend fun togglePin(id: Long, timestamp: Long)
}
