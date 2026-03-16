package com.example.taskmaster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.ui.screens.components.SortDropdown
import com.example.taskmaster.viewmodel.TaskViewModel

/**
 * Task List Screen - displays all tasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    onEditTask: (String) -> Unit,
    onSignOut: () -> Unit,
    onSettings: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.sortedTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val selectedSort by viewModel.sortOption.collectAsState()
    val isDeleteMode by viewModel.isDeleteMode.collectAsState()
    val selectedTaskIds by viewModel.selectedTaskIds.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
        viewModel.resetOperationResult()
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("TaskMaster3000") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isDeleteMode) {
                FloatingActionButton(
                    onClick = { if (selectedTaskIds.isNotEmpty()) showConfirmDialog = true },
                    containerColor = if (selectedTaskIds.isNotEmpty())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedTaskIds.isNotEmpty())
                        MaterialTheme.colorScheme.onError
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Selected Tasks")
                }
            } else {
                FloatingActionButton(onClick = onAddTask) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { paddingValues ->
        if (showConfirmDialog) {
            DeleteConfirmationDialog(
                count = selectedTaskIds.size,
                onConfirm = {
                    viewModel.deleteSelectedTasks()
                    showConfirmDialog = false
                },
                onDismiss = { showConfirmDialog = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SortDropdown(
                        selectedOption = selectedSort,
                        onSortSelected = { viewModel.setSortOption(it) }
                    )
                }
                TextButton(
                    onClick = { viewModel.toggleDeleteMode() },
                    enabled = !isLoading
                ) {
                    Text(if (isDeleteMode) "Cancel" else "Delete Tasks")
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadTasks() }) {
                                Text("Retry")
                            }
                        }
                    }
                    tasks.isEmpty() -> {
                        Text(
                            text = "No tasks yet. Tap + to add one!",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tasks, key = { it.id ?: "" }) { task ->
                                task.id?.let { taskId ->
                                    TaskItem(
                                        task = task,
                                        onTaskClick = {
                                            if (!isDeleteMode) onEditTask(taskId)
                                            else viewModel.toggleTaskSelection(taskId)
                                        },
                                        onToggleComplete = { viewModel.toggleTaskCompletion(taskId) },
                                        onDeleteTask = { viewModel.deleteTask(taskId) },
                                        isDeleteMode = isDeleteMode,
                                        isSelected = taskId in selectedTaskIds,
                                        onToggleSelection = { viewModel.toggleTaskSelection(taskId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: com.example.taskmaster.data.model.Task,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDeleteTask: () -> Unit,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: () -> Unit = {}
) {
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors()
    }
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onErrorContainer
    else MaterialTheme.colorScheme.onSurface
    val padding = 16.dp
    val cardAlpha = if (isDeleteMode) 0.5f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .alpha(cardAlpha),
            onClick = onTaskClick,
            colors = cardColors
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor
                    )
                    task.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = task.deadline?.let { "Deadline: $it" } ?: "No deadline",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (task.isCompleted) {
                    Text(
                        text = "Completed",
                        color = Color(0xFF00FF00), // OK-green color
                        modifier = Modifier.align(Alignment.Top)
                    )
                } else {
                    Text(
                        text = "Priority: ${task.priority}",
                        color = Color.Red, // Assuming priority is highlighted in red
                        modifier = Modifier.align(Alignment.Top)
                    )
                }
            }
        }
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() },
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Tasks") },
        text = { Text("Delete $count selected task(s)? This cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}