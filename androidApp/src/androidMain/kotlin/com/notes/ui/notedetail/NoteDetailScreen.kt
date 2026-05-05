package com.notes.ui.notedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.domain.model.Category
import com.notes.domain.model.Priority
import com.notes.presentation.notedetail.NoteDetailEvent
import com.notes.presentation.notedetail.NoteDetailViewModel
import com.notes.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                NoteDetailEvent.NoteSaved -> onNavigateBack()
                NoteDetailEvent.NavigateBack -> onNavigateBack()
                is NoteDetailEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NoteDetailTopBar(
                isEditMode = state.isEditMode,
                onBack = viewModel::onDiscardChanges
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = VanillaCustard
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Title Field
            TextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = { 
                    Text(
                        "NOTE TITLE", 
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                        color = PrussianBlue.copy(alpha = 0.3f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp, color = PrussianBlue),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = PrussianBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            HorizontalDivider(color = PrussianBlue.copy(alpha = 0.15f), thickness = 1.dp)
            
            // Creation Date
            state.createdAt?.let {
                Text(
                    text = "CREATED ON ${com.notes.domain.util.DateTimeUtil.formatNoteDate(it).uppercase()}",
                    fontFamily = Oswald,
                    fontSize = 10.sp,
                    color = PrussianBlue.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(Modifier.height(24.dp))

            // Priority Selector
            SectionTitle("SELECT PRIORITY")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { priority ->
                    PriorityChip(
                        priority = priority,
                        isSelected = state.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Category Selector
            SectionTitle("CATEGORY")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Category.entries) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = state.category == category,
                        onClick = { viewModel.onCategoryChange(category) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Static Tags display (compact)
            if (state.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    state.tags.forEach { tag ->
                        Surface(
                            color = LightGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                Text(text = tag, fontFamily = Oswald, fontSize = 10.sp, color = PrussianBlue)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Content with Notebook design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
                    .background(VanillaCustard)
                    .drawBehind {
                        val lineHeight = 30.sp.toPx()
                        val verticalPadding = 0.dp.toPx()
                        var y = verticalPadding + lineHeight
                        while (y < size.height) {
                            drawLine(
                                color = PrussianBlue.copy(alpha = 0.1f),
                                start = androidx.compose.ui.geometry.Offset(0f, y),
                                end = androidx.compose.ui.geometry.Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                            y += lineHeight
                        }
                    }
            ) {
                TextField(
                    value = state.content,
                    onValueChange = viewModel::onContentChange,
                    placeholder = { 
                        Text(
                            "Start capturing your thoughts...", 
                            fontFamily = Oswald,
                            color = PrussianBlue.copy(alpha = 0.3f)
                        ) 
                    },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = Oswald, 
                        color = PrussianBlue,
                        lineHeight = 30.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = PrussianBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(Modifier.height(80.dp)) // Save button space
        }

        // Save Button (Fixed at bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = viewModel::onSave,
                enabled = state.isValid && !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FieryTerracotta,
                    contentColor = VanillaCustard,
                    disabledContainerColor = FieryTerracotta.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = VanillaCustard)
                } else {
                    Text(
                        text = "SAVE",
                        fontFamily = Oswald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = Oswald,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = PrussianBlue.copy(alpha = 0.6f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun PriorityChip(
    priority: Priority,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = getPriorityColor(priority).let { if (isSelected) it else it.copy(alpha = 0.6f) },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .height(36.dp)
            .let { 
                if (isSelected) it.border(2.dp, PrussianBlue, RoundedCornerShape(24.dp)) else it 
            }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = priority.name,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = getPriorityTextColor(priority)
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = LightGold,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .height(36.dp)
            .let { 
                if (isSelected) it.border(2.dp, SandyBrown, RoundedCornerShape(24.dp)) else it 
            }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = category.label.uppercase(),
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PrussianBlue
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailTopBar(
    isEditMode: Boolean,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditMode) "EDIT NOTE" else "NEW NOTE",
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                color = VanillaCustard,
                letterSpacing = 2.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SandyBrown)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrussianBlue
        )
    )
}

// FlowRow is available in foundation 1.5.0+, but for older versions we might need a custom layout.
// I'll assume foundation 1.5.0+ is available as per libs.versions.toml (compose-multiplatform 1.7.0)

