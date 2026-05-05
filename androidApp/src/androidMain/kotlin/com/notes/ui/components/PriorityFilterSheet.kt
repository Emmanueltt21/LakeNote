package com.notes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.domain.model.Priority
import com.notes.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityFilterSheet(
    selectedPriority: Priority?,
    onPrioritySelected: (Priority?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VanillaCustard,
        scrimColor = PrussianBlue.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 32.dp, height = 2.dp)
                    .background(PrussianBlue.copy(alpha = 0.2f), RoundedCornerShape(1.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter by Priority",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Oswald,
                    color = PrussianBlue
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PrussianBlue)
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Priority.entries.forEach { priority ->
                    PriorityRow(
                        priority = priority,
                        isSelected = selectedPriority == priority,
                        onClick = { onPrioritySelected(priority) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(
                onClick = { onPrioritySelected(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CLEAR FILTER",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    color = FieryTerracotta,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun PriorityRow(
    priority: Priority,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(getPriorityColor(priority))
            )
            
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = priority.name,
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrussianBlue,
                    modifier = Modifier.alpha(if (isSelected) 1.0f else 0.6f)
                )
                
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = SandyBrown
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(2.dp, PrussianBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    )
                }
            }
        }
    }
}
