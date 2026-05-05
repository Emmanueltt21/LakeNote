package com.notes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notes.domain.model.Note
import com.notes.ui.theme.Oswald
import com.notes.ui.theme.PrussianBlue
import com.notes.ui.theme.SandyBrown
import com.notes.ui.theme.VanillaCustard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrussianBlue else PrussianBlue.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = VanillaCustard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PriorityBadge(priority = note.priority)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        IconButton(onClick = onTogglePin, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Unpin",
                                tint = SandyBrown,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onLongClick() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrussianBlue,
                                checkmarkColor = VanillaCustard
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                color = PrussianBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Oswald,
                color = PrussianBlue.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (note.tags.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    note.tags.forEach { tag ->
                        TagChip(tag = tag)
                    }
                }
            }
        }
    }
}
