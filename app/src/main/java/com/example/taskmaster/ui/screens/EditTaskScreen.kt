package com.example.taskmaster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.model.TaskPriority
import com.example.taskmaster.viewmodel.TaskViewModel

/**
 * Edit Task Screen - form for editing an existing task
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val task = viewModel.getTaskById(taskId)

    // Initialize state with existing task data
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var deadline by remember { mutableStateOf(task?.deadline ?: "") }

    if (task == null) {
        // Task not found
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Task not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Priority Selector
            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { priorityOption ->
                    FilterChip(
                        selected = priority == priorityOption,
                        onClick = { priority = priorityOption },
                        label = { Text(priorityOption.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Deadline Field (simplified for now)
            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Deadline (Optional, ISO format)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2026-02-15T17:00:00") }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Update Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val updatedTask = task.copy(
                            title = title,
                            description = description.ifBlank { null },
                            priority = priority,
                            deadline = deadline.ifBlank { null }
                        )
                        viewModel.updateTask(updatedTask)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Update Task")
            }

            // Delete Button
            OutlinedButton(
                onClick = {
                    viewModel.deleteTask(taskId)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Task")
            }
        }
    }
}

