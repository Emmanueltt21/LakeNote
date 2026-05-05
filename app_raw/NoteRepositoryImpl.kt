package com.notes.data.repository

import com.notes.data.local.dao.NoteDao
import com.notes.data.mapper.toDomain
import com.notes.data.mapper.toEntity
import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.domain.model.Priority
import com.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * NoteRepositoryImpl — the concrete implementation of the domain contract.
 *
 * Lives in the DATA layer. Domain layer never imports this class directly.
 * Dependency injection (Koin) wires NoteRepository → NoteRepositoryImpl at runtime.
 *
 * Responsibilities:
 * 1. Delegate to the DAO for all DB operations
 * 2. Map entities ↔ domain models using NoteMapper
 * 3. Handle sort routing (NoteSort enum → correct DAO query)
 * 4. Inject timestamps for archive/pin operations
 *
 * What this class does NOT do:
 * - Business logic (that's Use Cases)
 * - Data validation (that's the domain model)
 * - UI state management (that's the ViewModel)
 */
class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun observeNotes(sort: NoteSort): Flow<List<Note>> {
        val entityFlow = when (sort) {
            NoteSort.BY_UPDATED_DESC  -> dao.observeNotesByUpdated()
            NoteSort.BY_CREATED_DESC  -> dao.observeNotesByCreated()
            NoteSort.BY_PRIORITY_DESC -> dao.observeNotesByPriority()
            NoteSort.BY_TITLE_ASC     -> dao.observeNotesByTitle()
        }
        return entityFlow.map { it.toDomain() }
    }

    override fun observeArchivedNotes(): Flow<List<Note>> =
        dao.observeArchivedNotes().map { it.toDomain() }

    override fun searchNotes(query: String): Flow<List<Note>> =
        dao.searchNotes(query).map { it.toDomain() }

    override fun observeNotesByPriority(priority: Priority): Flow<List<Note>> =
        dao.observeNotesByPriorityLevel(priority.ordinal).map { it.toDomain() }

    override suspend fun getNoteById(id: Long): Note? =
        dao.getNoteById(id)?.toDomain()

    override suspend fun saveNote(note: Note): Long =
        dao.upsertNote(note.toEntity())

    override suspend fun archiveNote(id: Long) {
        dao.archiveNote(id, timestamp = Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun deleteNote(id: Long) {
        dao.deleteNote(id)
    }

    override suspend fun togglePin(id: Long) {
        dao.togglePin(id, timestamp = Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun deleteNotes(ids: List<Long>) {
        dao.deleteNotes(ids)
    }
}
