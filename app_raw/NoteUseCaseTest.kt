package com.notes.domain.usecase

import com.notes.domain.model.Category
import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.domain.model.Priority
import com.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for use cases using a fake in-memory repository.
 *
 * Interview talking points:
 * - FakeNoteRepository implements NoteRepository — same interface used in production
 * - No Room, no Android context — pure Kotlin tests run on JVM and iOS
 * - kotlinx-coroutines-test provides runTest {} for suspend functions
 * - This is why Clean Architecture pays off: swappable implementations
 */
class NoteUseCaseTest {

    private val fakeRepo = FakeNoteRepository()
    private val saveNoteUseCase = SaveNoteUseCase(fakeRepo)
    private val getNotesUseCase = GetNotesUseCase(fakeRepo)
    private val getNoteByIdUseCase = GetNoteByIdUseCase(fakeRepo)
    private val deleteNoteUseCase = DeleteNoteUseCase(fakeRepo)
    private val togglePinUseCase = TogglePinNoteUseCase(fakeRepo)

    @Test
    fun `saveNote returns failure when title is blank`() = runTest {
        val blankTitleNote = Note(title = "", content = "content", priority = Priority.LOW)
        val result = saveNoteUseCase(blankTitleNote)
        assertTrue(result.isFailure)
    }

    @Test
    fun `saveNote returns success when title is valid`() = runTest {
        val note = Note(title = "Valid title", content = "content", priority = Priority.HIGH)
        val result = saveNoteUseCase(note)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getNoteById returns null for non-existent id`() = runTest {
        val note = getNoteByIdUseCase(999L)
        assertNull(note)
    }

    @Test
    fun `saved note can be retrieved by id`() = runTest {
        val note = Note(title = "Test Note", content = "Body", priority = Priority.MEDIUM)
        val idResult = saveNoteUseCase(note)
        val savedId = idResult.getOrThrow()
        val retrieved = getNoteByIdUseCase(savedId)
        assertNotNull(retrieved)
        assertEquals("Test Note", retrieved.title)
    }

    @Test
    fun `pinned notes appear first in list`() = runTest {
        val unpinned = Note(title = "Unpinned", content = "", priority = Priority.LOW)
        val pinned = Note(title = "Pinned", content = "", priority = Priority.LOW, isPinned = true)
        saveNoteUseCase(unpinned)
        saveNoteUseCase(pinned)

        // GetNotesUseCase applies pinned-first ordering
        getNotesUseCase().collect { notes ->
            assertEquals("Pinned", notes.first().title)
        }
    }

    @Test
    fun `deleteNote removes note permanently`() = runTest {
        val note = Note(title = "To Delete", content = "", priority = Priority.LOW)
        val id = saveNoteUseCase(note).getOrThrow()
        deleteNoteUseCase(id)
        assertNull(getNoteByIdUseCase(id))
    }
}

// --- Fake Repository (in-memory) ---

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun observeNotes(sort: NoteSort): Flow<List<Note>> =
        notes.map { it.filter { n -> !n.isArchived } }

    override fun observeArchivedNotes(): Flow<List<Note>> =
        notes.map { it.filter { n -> n.isArchived } }

    override fun searchNotes(query: String): Flow<List<Note>> =
        notes.map { all ->
            all.filter { !it.isArchived && (it.title.contains(query, true) || it.content.contains(query, true)) }
        }

    override fun observeNotesByPriority(priority: Priority): Flow<List<Note>> =
        notes.map { it.filter { n -> n.priority == priority && !n.isArchived } }

    override suspend fun getNoteById(id: Long): Note? =
        notes.value.find { it.id == id }

    override suspend fun saveNote(note: Note): Long {
        val id = if (note.id == 0L) nextId++ else note.id
        val toSave = note.copy(id = id)
        notes.value = notes.value.filter { it.id != id } + toSave
        return id
    }

    override suspend fun archiveNote(id: Long) {
        notes.value = notes.value.map { if (it.id == id) it.copy(isArchived = true) else it }
    }

    override suspend fun deleteNote(id: Long) {
        notes.value = notes.value.filter { it.id != id }
    }

    override suspend fun togglePin(id: Long) {
        notes.value = notes.value.map { if (it.id == id) it.copy(isPinned = !it.isPinned) else it }
    }

    override suspend fun deleteNotes(ids: List<Long>) {
        notes.value = notes.value.filter { it.id !in ids }
    }
}
