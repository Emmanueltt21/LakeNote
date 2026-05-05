package com.notes.presentation.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.domain.model.Category
import com.notes.domain.model.Note
import com.notes.domain.model.Priority
import com.notes.domain.usecase.GetNoteByIdUseCase
import com.notes.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * NoteDetailViewModel — handles both CREATE and EDIT modes.
 *
 * SavedStateHandle:
 * - Provides nav arguments (noteId) and survives process death
 * - noteId == null → CREATE mode, noteId != null → EDIT mode
 *
 * Form state design:
 * - Each field is a separate MutableStateFlow for fine-grained updates
 * - combine() merges them into a single FormState for the UI
 * - Derived state (isValid, hasChanges) computed reactively
 */
class NoteDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.get<Long>("noteId")?.takeIf { it != -1L }
    private var originalNote: Note? = null

    // --- Form field states ---
    private val _title = MutableStateFlow("")
    private val _content = MutableStateFlow("")
    private val _priority = MutableStateFlow(Priority.MEDIUM)
    private val _category = MutableStateFlow(Category.GENERAL)
    private val _tags = MutableStateFlow<List<String>>(emptyList())
    private val _isSaving = MutableStateFlow(false)

    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events: SharedFlow<NoteDetailEvent> = _events.asSharedFlow()

    val uiState: StateFlow<NoteDetailState> = combine(
        _title, _content, _priority, _category, _tags, _isSaving
    ) { values ->
        // Destructure from the array combine emits for 6+ flows
        @Suppress("UNCHECKED_CAST")
        val title = values[0] as String
        val content = values[1] as String
        val priority = values[2] as Priority
        val category = values[3] as Category
        val tags = values[4] as List<String>
        val isSaving = values[5] as Boolean

        NoteDetailState(
            noteId = noteId,
            title = title,
            content = content,
            priority = priority,
            category = category,
            tags = tags,
            isSaving = isSaving,
            isEditMode = noteId != null,
            isValid = title.isNotBlank() && title.length <= Note.MAX_TITLE_LENGTH,
            hasChanges = originalNote?.let {
                it.title != title || it.content != content ||
                it.priority != priority || it.category != category ||
                it.tags != tags
            } ?: (title.isNotBlank() || content.isNotBlank()),
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteDetailState(),
    )

    init {
        noteId?.let { loadNote(it) }
    }

    private fun loadNote(id: Long) = viewModelScope.launch {
        getNoteByIdUseCase(id)?.also { note ->
            originalNote = note
            _title.value = note.title
            _content.value = note.content
            _priority.value = note.priority
            _category.value = note.category
            _tags.value = note.tags
        } ?: _events.emit(NoteDetailEvent.ShowError("Note not found"))
    }

    fun onTitleChange(value: String) { _title.value = value }
    fun onContentChange(value: String) { _content.value = value }
    fun onPriorityChange(priority: Priority) { _priority.value = priority }
    fun onCategoryChange(category: Category) { _category.value = category }

    fun onAddTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isNotBlank() && trimmed !in _tags.value) {
            _tags.update { it + trimmed }
        }
    }

    fun onRemoveTag(tag: String) {
        _tags.update { it - tag }
    }

    fun onSave() = viewModelScope.launch {
        _isSaving.value = true
        val noteToSave = Note(
            id = noteId ?: 0L,
            title = _title.value.trim(),
            content = _content.value,
            priority = _priority.value,
            category = _category.value,
            tags = _tags.value,
            isPinned = originalNote?.isPinned ?: false,
            createdAt = originalNote?.createdAt ?: kotlinx.datetime.Clock.System.now(),
        )
        saveNoteUseCase(noteToSave)
            .onSuccess { _events.emit(NoteDetailEvent.NoteSaved) }
            .onFailure { _events.emit(NoteDetailEvent.ShowError(it.message ?: "Save failed")) }
        _isSaving.value = false
    }

    fun onDiscardChanges() = viewModelScope.launch {
        _events.emit(NoteDetailEvent.NavigateBack)
    }
}

// --- State ---

data class NoteDetailState(
    val noteId: Long? = null,
    val title: String = "",
    val content: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.GENERAL,
    val tags: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val isValid: Boolean = false,
    val hasChanges: Boolean = false,
)

// --- Events ---

sealed interface NoteDetailEvent {
    data object NoteSaved : NoteDetailEvent
    data object NavigateBack : NoteDetailEvent
    data class ShowError(val message: String) : NoteDetailEvent
}
