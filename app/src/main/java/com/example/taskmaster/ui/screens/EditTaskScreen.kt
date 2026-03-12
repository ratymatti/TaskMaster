package com.example.taskmaster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.ui.screens.components.*
import com.example.taskmaster.viewmodel.TaskViewModel

/**
 * Edit Task Screen - form for editing an existing task.
 */
@Composable
fun EditTaskScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val task = viewModel.getTaskById(taskId)

    var formState by remember(task) {
        mutableStateOf(task?.let { TaskFormState.fromTask(it) } ?: TaskFormState())
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    val navigation = rememberTaskFormNavigation(operationResult, onNavigateBack)

    if (task == null) {
        TaskNotFoundScreen(onNavigateBack = onNavigateBack)
        return
    }

    TaskFormScaffold(
        title = "Edit Task",
        onNavigateBack = onNavigateBack,
        error = error
    ) {
        TaskFormFields(
            title = formState.title,
            onTitleChange = { formState = formState.copy(title = it) },
            description = formState.description,
            onDescriptionChange = { formState = formState.copy(description = it) },
            priority = formState.priority,
            onPriorityChange = { formState = formState.copy(priority = it) },
            deadline = formState.deadline,
            onDeadlineChange = { formState = formState.copy(deadline = it) },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.weight(1f))

        UpdateTaskButton(
            onClick = {
                navigation.markOperationInitiated()
                viewModel.updateTask(formState.toTask(existingTask = task))
            },
            enabled = formState.isValid() && !isLoading,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        DeleteTaskButton(
            onClick = {
                navigation.markOperationInitiated()
                viewModel.deleteTask(taskId)
            },
            enabled = !isLoading,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
