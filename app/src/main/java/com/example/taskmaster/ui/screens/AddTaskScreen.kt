package com.example.taskmaster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.ui.screens.components.*
import com.example.taskmaster.viewmodel.TaskViewModel

/**
 * Add Task Screen - form for creating a new task.
 */
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    var formState by remember { mutableStateOf(TaskFormState()) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    val navigation = rememberTaskFormNavigation(operationResult, onNavigateBack)

    TaskFormScaffold(
        title = "Add Task",
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

        SaveTaskButton(
            onClick = {
                navigation.markOperationInitiated()
                viewModel.addTask(formState.toTask())
            },
            enabled = formState.isValid() && !isLoading,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
