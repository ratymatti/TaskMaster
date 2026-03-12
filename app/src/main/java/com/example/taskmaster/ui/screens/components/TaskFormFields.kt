package com.example.taskmaster.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmaster.data.model.TaskPriority

/**
 * Reusable composable for all task form input fields.
 */
@Composable
fun TaskFormFields(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    deadline: String,
    onDeadlineChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Field
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled
        )

        // Description Field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            enabled = enabled
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
                    onClick = { onPriorityChange(priorityOption) },
                    label = { Text(priorityOption.name) },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
            }
        }

        // Deadline Field
        OutlinedTextField(
            value = deadline,
            onValueChange = onDeadlineChange,
            label = { Text("Deadline (Optional, ISO format)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("2026-02-15T17:00:00") },
            enabled = enabled
        )
    }
}

