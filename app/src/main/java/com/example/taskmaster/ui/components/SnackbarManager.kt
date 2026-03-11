package com.example.taskmaster.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Reusable Snackbar handler component
 *
 * @param snackbarMessage The message to display in the Snackbar. If null, nothing is shown.
 * @param snackbarHostState The SnackbarHostState to use for displaying the Snackbar
 * @param onMessageShown Callback invoked after the message has been shown
 */
@Composable
fun SnackbarHandler(
    snackbarMessage: String?,
    snackbarHostState: SnackbarHostState,
    onMessageShown: () -> Unit
) {
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onMessageShown()
        }
    }
}

