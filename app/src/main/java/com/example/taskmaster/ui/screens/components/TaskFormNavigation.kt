package com.example.taskmaster.ui.screens.components

import androidx.compose.runtime.*
import com.example.taskmaster.viewmodel.TaskOperationResult

/**
 * Holds the callback used by the caller to mark when an operation has been initiated.
 */
data class TaskFormNavigationState(
    val markOperationInitiated: () -> Unit
)

/**
 * Remembers navigation state for task form screens.
 * Navigates back on a successful operation, but only if the operation was initiated
 * from the current screen (prevents spurious navigation from stale results).
 */
@Composable
fun rememberTaskFormNavigation(
    operationResult: TaskOperationResult,
    onNavigateBack: () -> Unit
): TaskFormNavigationState {
    var operationInitiated by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(operationResult) {
        if (operationInitiated && !hasNavigated) {
            when (operationResult) {
                is TaskOperationResult.Success -> {
                    hasNavigated = true
                    onNavigateBack()
                }
                else -> { /* No action */ }
            }
        }
    }

    return TaskFormNavigationState(
        markOperationInitiated = { operationInitiated = true }
    )
}

