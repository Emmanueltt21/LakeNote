package com.notes.domain.repository

import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.domain.model.Priority
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface — defined in the DOMAIN layer.
 *
 * This is the Dependency Inversion Principle in practice:
 * - Domain layer DEFINES the contract (this interface)
 * - Data layer IMPLEMENTS it (NoteRepositoryImpl)
 * - Domain layer never imports from the data layer
 *
 * Benefits for interview discussion:
 * 1. Testability — swap real DB with FakeNoteRepository in tests
 * 2. Platform-agnostic — interface is in commonMain, impl can vary per platform
 * 3. Clean separation — UI/Domain never know if data comes from Room, API, or cache
 */
interface NoteRepository {

    /**
     * Observe all non-archived notes as a reactive stream.
     * Flow keeps the UI in sync automatically when data changes.
     */
    fun observeNotes(sort: NoteSort = NoteSort.BY_UPDATED_DESC): Flow<List<Note>>

    /**
     * Observe archived notes separately — keeps main list clean.
     */
    fun observeArchivedNotes(): Flow<List<Note>>

    /**
     * Full-text search across title and content.
     * Returns Flow so results update as user types (combine with debounce).
     */
    fun searchNotes(query: String): Flow<List<Note>>

    /**
     * Filter notes by priority level.
     */
    fun observeNotesByPriority(priority: Priority): Flow<List<Note>>

    /**
     * Single note lookup — returns null if not found (prefer over throwing).
     */
    suspend fun getNoteById(id: Long): Note?

    /**
     * Upsert: inserts if id == 0, updates if id > 0.
     * Returns the persisted note's id (useful after insert).
     */
    suspend fun saveNote(note: Note): Long

    /**
     * Soft delete via archiving — avoids accidental data loss.
     */
    suspend fun archiveNote(id: Long)

    /**
     * Hard delete — used after confirmation or from archive view.
     */
    suspend fun deleteNote(id: Long)

    /**
     * Toggle pin state — pinned notes float to top of list.
     */
    suspend fun togglePin(id: Long)

    /**
     * Batch delete — for bulk operations from selection mode.
     */
    suspend fun deleteNotes(ids: List<Long>)
}
