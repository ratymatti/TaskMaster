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
import com.example.taskmaster.viewmodel.TaskOperationResult
import com.example.taskmaster.viewmodel.TaskViewModel

/**
 * Add Task Screen - form for creating a new task
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var deadline by remember { mutableStateOf("") }

    // Observe ViewModel states
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    // Reset operation result when entering the screen
    LaunchedEffect(Unit) {
        viewModel.resetOperationResult()
    }

    // Handle operation result
    LaunchedEffect(operationResult) {
        when (operationResult) {
            is TaskOperationResult.Success -> {
                onNavigateBack()
            }
            else -> { /* No action needed */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") },
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
            // Error message display
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = !isLoading
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
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    )
                }
            }

            // Deadline Field (simplified for now)
            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Deadline (Optional, ISO format)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2026-02-15T17:00:00") },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = Task(
                            title = title,
                            description = description.ifBlank { null },
                            priority = priority,
                            deadline = deadline.ifBlank { null }
                        )
                        viewModel.addTask(task)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Task")
                }
            }
        }
    }
}

