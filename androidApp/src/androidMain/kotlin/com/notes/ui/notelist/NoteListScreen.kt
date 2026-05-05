package com.notes.ui.notelist

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.domain.model.Note
import com.notes.domain.model.NoteSort
import com.notes.presentation.notelist.NoteListEvent
import com.notes.presentation.notelist.NoteListState
import com.notes.presentation.notelist.NoteListViewModel
import com.notes.ui.components.NoteCard
import com.notes.ui.components.PriorityFilterSheet
import com.notes.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: NoteListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is NoteListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    )
                }
                is NoteListEvent.NavigateToDetail -> onNavigateToDetail(event.noteId)
                NoteListEvent.NavigateToCreate -> onNavigateToCreate()
            }
        }
    }

    Scaffold(
        topBar = {
            NoteListTopBar(
                state = state,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearchToggle = viewModel::onSearchToggle,
                onSortToggle = { showFilterSheet = true },
                onClearSelection = viewModel::onClearSelection,
                onArchiveSelected = viewModel::onArchiveSelected,
                onDeleteSelected = viewModel::onDeleteSelected
            )
        },
        bottomBar = {
            NoteListBottomBar()
        },
        floatingActionButton = {
            if ((state as? NoteListState.Success)?.isSelectionMode != true) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = SandyBrown,
                    contentColor = PrussianBlue,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Note", modifier = Modifier.size(32.dp))
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = PrussianBlue,
                    contentColor = VanillaCustard,
                    actionColor = SandyBrown
                )
            }
        },
        containerColor = VanillaCustard
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val s = state) {
                NoteListState.Loading -> LoadingContent()
                is NoteListState.Error -> ErrorContent(s.message)
                is NoteListState.Success -> NoteListContent(
                    state = s,
                    onNoteClick = viewModel::onNoteClick,
                    onNoteLongClick = viewModel::onNoteSelected,
                    onTogglePin = viewModel::onTogglePin
                )
            }
        }
    }

    if (showFilterSheet) {
        PriorityFilterSheet(
            selectedPriority = null, // Filter not yet in state, just for UI show
            onPrioritySelected = { priority ->
                // Sort by priority logic
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun NoteListContent(
    state: NoteListState.Success,
    onNoteClick: (Long) -> Unit,
    onNoteLongClick: (Long) -> Unit,
    onTogglePin: (Long) -> Unit
) {
    if (state.notes.isEmpty()) {
        EmptyContent(isSearchMode = state.query.isNotBlank())
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val (pinned, unpinned) = state.notes.partition { it.isPinned }
            
            if (pinned.isNotEmpty()) {
                item {
                    SectionHeader(title = "Pinned Notes", icon = Icons.Default.Add)
                }
                items(pinned, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        isSelected = note.id in state.selectedIds,
                        isSelectionMode = state.isSelectionMode,
                        onClick = { onNoteClick(note.id) },
                        onLongClick = { onNoteLongClick(note.id) },
                        onTogglePin = { onTogglePin(note.id) },
                        modifier = Modifier.animateItem()
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            if (unpinned.isNotEmpty()) {
                item {
                    SectionHeader(title = "All Notes", icon = null)
                }
                items(unpinned, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        isSelected = note.id in state.selectedIds,
                        isSelectionMode = state.isSelectionMode,
                        onClick = { onNoteClick(note.id) },
                        onLongClick = { onNoteLongClick(note.id) },
                        onTogglePin = { onTogglePin(note.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        if (icon != null) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = SandyBrown, 
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title.uppercase(),
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = PrussianBlue.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListTopBar(
    state: NoteListState,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onSortToggle: () -> Unit,
    onClearSelection: () -> Unit,
    onArchiveSelected: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    val successState = state as? NoteListState.Success
    val isSearchActive = successState?.isSearchActive == true
    val isSelectionMode = successState?.isSelectionMode == true

    TopAppBar(
        title = {
            if (isSelectionMode) {
                Text(
                    text = "${successState?.selectedIds?.size} SELECTED",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    color = VanillaCustard
                )
            } else if (isSearchActive) {
                TextField(
                    value = successState?.query ?: "",
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search your thoughts...", color = VanillaCustard.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = VanillaCustard,
                        focusedTextColor = VanillaCustard,
                        unfocusedTextColor = VanillaCustard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            } else {
                Text(
                    text = "NOTES",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    color = VanillaCustard,
                    letterSpacing = 2.sp
                )
            }
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = VanillaCustard)
                }
            } else {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = VanillaCustard)
                }
            }
        },
        actions = {
            if (isSelectionMode) {
                IconButton(onClick = onArchiveSelected) {
                    Icon(Icons.Default.Settings, contentDescription = "Archive", tint = VanillaCustard)
                }
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = VanillaCustard)
                }
            } else {
                IconButton(onClick = onSearchToggle) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = SandyBrown)
                }
                IconButton(onClick = onSortToggle) {
                    Icon(Icons.Default.Menu, contentDescription = "Sort", tint = SandyBrown)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrussianBlue
        )
    )
}

@Composable
private fun NoteListBottomBar() {
    NavigationBar(
        containerColor = PrussianBlue,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Menu, contentDescription = "Notes") },
            label = { Text("NOTES", fontFamily = Oswald, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SandyBrown,
                selectedTextColor = SandyBrown,
                unselectedIconColor = VanillaCustard.copy(alpha = 0.4f),
                unselectedTextColor = VanillaCustard.copy(alpha = 0.4f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
            label = { Text("TASKS", fontFamily = Oswald, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = VanillaCustard.copy(alpha = 0.4f),
                unselectedTextColor = VanillaCustard.copy(alpha = 0.4f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Settings, contentDescription = "Archive") },
            label = { Text("ARCHIVE", fontFamily = Oswald, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = VanillaCustard.copy(alpha = 0.4f),
                unselectedTextColor = VanillaCustard.copy(alpha = 0.4f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("SETTINGS", fontFamily = Oswald, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = VanillaCustard.copy(alpha = 0.4f),
                unselectedTextColor = VanillaCustard.copy(alpha = 0.4f),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SandyBrown)
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = FieryTerracotta, fontFamily = Oswald)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = SandyBrown)) {
                Text("RETRY", color = PrussianBlue)
            }
        }
    }
}

@Composable
private fun EmptyContent(isSearchMode: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                if (isSearchMode) Icons.Default.Search else Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = SandyBrown
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isSearchMode) "NO MATCHES FOUND" else "NO NOTES YET",
                fontFamily = Oswald,
                color = PrussianBlue.copy(alpha = 0.6f)
            )
        }
    }
}
