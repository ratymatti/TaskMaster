package com.example.taskmaster.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Shown when a task cannot be found (e.g. deleted externally or invalid ID).
 */
@Composable
fun TaskNotFoundScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Task not found")
            TextButton(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
    }
}

