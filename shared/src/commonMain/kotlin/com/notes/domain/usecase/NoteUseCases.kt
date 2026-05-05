package com.notes.domain.usecase

import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.domain.model.Priority
import com.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Use Cases — the application's business logic entry points.
 *
 * Interview talking points:
 * - Each use case is a Single Responsibility: one class, one action
 * - They are the boundary between Presentation and Domain layers
 * - ViewModels depend on use cases, NEVER on repositories directly
 * - Use cases are easily unit-testable with a fake repository
 * - They can compose other use cases or apply cross-cutting logic (logging, analytics)
 *
 * Naming convention: <Verb><Noun>UseCase (e.g. GetNotesUseCase, SaveNoteUseCase)
 */

/**
 * Retrieves a reactive stream of notes, sorted by user preference.
 * Pinned notes always appear first — this is business logic, not UI logic.
 */
class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(sort: NoteSort = NoteSort.BY_UPDATED_DESC): Flow<List<Note>> =
        repository.observeNotes(sort).map { notes ->
            // Business rule: pinned notes always float to top
            val (pinned, unpinned) = notes.partition { it.isPinned }
            pinned + unpinned
        }
}

/**
 * Saves a note after validating domain rules.
 * Returns a Result to force callers to handle the failure case.
 */
class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Result<Long> {
        if (!note.isValid) {
            return Result.failure(
                IllegalArgumentException("Note title cannot be empty or exceed ${Note.MAX_TITLE_LENGTH} characters")
            )
        }
        return runCatching {
            repository.saveNote(note.copy(updatedAt = Clock.System.now()))
        }
    }
}

/**
 * Soft-deletes a note by archiving it first.
 * Provides a safety net before permanent deletion.
 */
class ArchiveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        runCatching { repository.archiveNote(id) }
}

/**
 * Permanently deletes a note — should only be called after user confirmation.
 */
class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        runCatching { repository.deleteNote(id) }

    suspend fun deleteMany(ids: List<Long>): Result<Unit> =
        runCatching { repository.deleteNotes(ids) }
}

/**
 * Retrieves a single note by ID for the detail/edit screen.
 */
class GetNoteByIdUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Note? = repository.getNoteById(id)
}

/**
 * Search notes with debounce-ready Flow emission.
 * The ViewModel applies debounce; use case stays pure.
 */
class SearchNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> =
        if (query.isBlank()) repository.observeNotes()
        else repository.searchNotes(query.trim())
}

/**
 * Toggles pin state — simple but isolated for testability.
 */
class TogglePinNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        runCatching { repository.togglePin(id) }
}

/**
 * Filters notes by priority — useful for a priority-filtered view.
 */
class GetNotesByPriorityUseCase(private val repository: NoteRepository) {
    operator fun invoke(priority: Priority): Flow<List<Note>> =
        repository.observeNotesByPriority(priority)
}

/**
 * Retrieves archived notes for the archive screen.
 */
class GetArchivedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.observeArchivedNotes()
}
