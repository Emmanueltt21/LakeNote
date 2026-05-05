package com.notes.presentation.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.domain.model.Priority
import com.notes.domain.usecase.ArchiveNoteUseCase
import com.notes.domain.usecase.DeleteNoteUseCase
import com.notes.domain.usecase.GetNotesUseCase
import com.notes.domain.usecase.SearchNotesUseCase
import com.notes.domain.usecase.TogglePinNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * NoteListViewModel — the Presentation layer's state manager.
 *
 * Architecture pattern: MVI (Model-View-Intent)
 * - State: single immutable NoteListState sealed class
 * - Events: one-time side effects via SharedFlow (snackbar, navigation)
 * - Actions: functions that process user intents
 *
 * Interview talking points:
 * - ViewModel survives configuration changes (screen rotation)
 * - viewModelScope auto-cancels all coroutines on ViewModel destruction
 * - StateFlow vs SharedFlow:
 *     StateFlow = current state (always has a value, replayed on collect)
 *     SharedFlow = one-time events (snackbar, navigation)
 * - flatMapLatest: cancels previous search when query changes (key for search)
 * - debounce: avoids a DB query on every keystroke
 * - combine: merges sort + notes flows reactively
 */
class NoteListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val togglePinNoteUseCase: TogglePinNoteUseCase,
) : ViewModel() {

    // --- Internal mutable state ---
    private val _searchQuery = MutableStateFlow("")
    private val _sort = MutableStateFlow(NoteSort.BY_UPDATED_DESC)
    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _isSearchActive = MutableStateFlow(false)

    // --- One-time events (snackbar, navigation) ---
    private val _events = MutableSharedFlow<NoteListEvent>()
    val events: SharedFlow<NoteListEvent> = _events.asSharedFlow()

    /**
     * UI state as a single StateFlow — the UI observes exactly one source of truth.
     *
     * combine() merges multiple flows into one.
     * flatMapLatest() in the search branch cancels the previous query
     * when a new search term arrives.
     */
    // --- Derived data flows ---

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val _notesFlow = combine(
        _searchQuery.debounce(300),
        _sort
    ) { query, sort ->
        query to sort
    }.flatMapLatest { (query, sort) ->
        if (query.isBlank()) {
            getNotesUseCase(sort)
        } else {
            searchNotesUseCase(query)
        }
    }.catch { 
        // Handle repository errors by emitting an empty list or specific error state
        emit(emptyList())
    }

    /**
     * UI state as a single StateFlow — the UI observes exactly one source of truth.
     *
     * We combine the immediate UI states (_searchQuery, _selectedIds, etc.)
     * with the async data flow (_notesFlow) to ensure the UI remains responsive
     * while data loads in the background.
     */
    val uiState: StateFlow<NoteListState> = combine(
        _searchQuery,
        _sort,
        _selectedIds,
        _isSearchActive,
        _notesFlow
    ) { query, sort, selectedIds, isSearchActive, notes ->
        NoteListState.Success(
            notes = notes,
            query = query,
            sort = sort,
            selectedIds = selectedIds,
            isSearchActive = isSearchActive,
            isSelectionMode = selectedIds.isNotEmpty(),
        ) as NoteListState
    }
    .catch { emit(NoteListState.Error(it.message ?: "Unknown error")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListState.Loading,
    )

    // --- User intent handlers ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchToggle() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) _searchQuery.value = ""
    }

    fun onSortChange(sort: NoteSort) {
        _sort.value = sort
    }

    fun onNoteSelected(id: Long) {
        _selectedIds.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun onClearSelection() {
        _selectedIds.value = emptySet()
    }

    fun onArchiveNote(id: Long) = viewModelScope.launch {
        archiveNoteUseCase(id).onSuccess {
            _events.emit(NoteListEvent.ShowSnackbar("Note archived", action = "Undo"))
        }.onFailure {
            _events.emit(NoteListEvent.ShowSnackbar("Failed to archive note"))
        }
    }

    fun onArchiveSelected() = viewModelScope.launch {
        val ids = _selectedIds.value.toList()
        ids.forEach { archiveNoteUseCase(it) }
        _selectedIds.value = emptySet()
        _events.emit(NoteListEvent.ShowSnackbar("${ids.size} notes archived"))
    }

    fun onDeleteSelected() = viewModelScope.launch {
        val ids = _selectedIds.value.toList()
        deleteNoteUseCase.deleteMany(ids)
        _selectedIds.value = emptySet()
        _events.emit(NoteListEvent.ShowSnackbar("${ids.size} notes deleted"))
    }

    fun onTogglePin(id: Long) = viewModelScope.launch {
        togglePinNoteUseCase(id)
    }

    fun onNoteClick(id: Long) = viewModelScope.launch {
        if (_selectedIds.value.isNotEmpty()) {
            onNoteSelected(id)
        } else {
            _events.emit(NoteListEvent.NavigateToDetail(id))
        }
    }
}

// --- State ---

sealed interface NoteListState {
    data object Loading : NoteListState
    data class Success(
        val notes: List<Note>,
        val query: String,
        val sort: NoteSort,
        val selectedIds: Set<Long>,
        val isSelectionMode: Boolean,
        val isSearchActive: Boolean,
    ) : NoteListState
    data class Error(val message: String) : NoteListState
}

// --- One-time events ---

sealed interface NoteListEvent {
    data class ShowSnackbar(val message: String, val action: String? = null) : NoteListEvent
    data class NavigateToDetail(val noteId: Long) : NoteListEvent
    data object NavigateToCreate : NoteListEvent
}
