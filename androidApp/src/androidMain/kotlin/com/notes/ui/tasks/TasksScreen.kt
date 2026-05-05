package com.notes.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.ui.theme.*

data class SampleTask(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

private val sampleTasks = listOf(
    SampleTask(1, "Buy groceries", "Pick up milk, eggs, bread, and fresh vegetables from the market."),
    SampleTask(2, "Morning workout", "Complete 30 minutes of cardio and 20 minutes of strength training."),
    SampleTask(3, "Read a book", "Read at least 2 chapters of 'Atomic Habits' by James Clear."),
    SampleTask(4, "Team meeting prep", "Prepare slides and talking points for the Monday standup meeting."),
    SampleTask(5, "Fix login bug", "Investigate and resolve the authentication timeout issue on the login page."),
    SampleTask(6, "Call the dentist", "Schedule a dental checkup appointment for next week.", true),
    SampleTask(7, "Clean the apartment", "Vacuum, mop the floors, and organize the kitchen shelves.", true),
    SampleTask(8, "Update resume", "Add recent project experience and refresh the skills section."),
    SampleTask(9, "Plan weekend trip", "Research hotels and activities for the Saturday getaway to the mountains."),
    SampleTask(10, "Pay electricity bill", "Complete the online payment before the due date on the 15th.", true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen() {
    var tasks by remember { mutableStateOf(sampleTasks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TASKS",
                        fontFamily = Oswald,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = VanillaCustard
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrussianBlue
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val pending = tasks.filter { !it.isCompleted }
            val completed = tasks.filter { it.isCompleted }

            if (pending.isNotEmpty()) {
                item {
                    Text(
                        text = "PENDING",
                        fontFamily = Oswald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(pending, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { id ->
                            tasks = tasks.map {
                                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                            }
                        }
                    )
                }
            }

            if (completed.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "COMPLETED",
                        fontFamily = Oswald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(completed, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { id ->
                            tasks = tasks.map {
                                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: SampleTask,
    onToggle: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle(task.id) },
                colors = CheckboxDefaults.colors(
                    checkedColor = SandyBrown,
                    uncheckedColor = PrussianBlue.copy(alpha = 0.5f),
                    checkmarkColor = PrussianBlue
                )
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontFamily = Oswald,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
