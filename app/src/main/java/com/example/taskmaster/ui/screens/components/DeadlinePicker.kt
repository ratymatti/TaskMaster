package com.example.taskmaster.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.*

fun formatDeadlineForDisplay(isoString: String): String? {
    if (isoString.isBlank()) return null
    return runCatching {
        val dt = LocalDateTime.parse(isoString)
        val monthAbbr = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        "$monthAbbr ${dt.dayOfMonth}, ${dt.year}"
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinePicker(
    deadline: String,
    modifier: Modifier = Modifier,
    onDeadlineChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val pendingDateMillis = remember { mutableStateOf<Long?>(null) }

    val initialMillis: Long = remember(deadline) {
        if (deadline.isBlank()) System.currentTimeMillis()
        else runCatching {
            LocalDateTime.parse(deadline)
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        }.getOrDefault(System.currentTimeMillis())
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val displayText = formatDeadlineForDisplay(deadline) ?: ""

    val interactionSource = remember { MutableInteractionSource() }

    if (enabled) {
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collectLatest { interaction ->
                if (interaction is PressInteraction.Press) {
                    showDatePicker.value = true
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            label = { Text("Deadline (Optional)") },
            placeholder = { Text("Tap to set a deadline") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = enabled,
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor         = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor       = MaterialTheme.colorScheme.outline,
                unfocusedLabelColor        = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor  = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTextColor          = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                disabledBorderColor        = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                disabledLabelColor         = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            ),
            trailingIcon = {
                if (enabled && deadline.isNotBlank()) {
                    IconButton(onClick = { onDeadlineChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear deadline")
                    }
                } else {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick deadline")
                }
            }
        )

        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker.value = true }
            ) {}
        }
    }

    // ── Date picker ──────────────────────────────────────────────────
    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            pendingDateMillis.value = selected
                            onDeadlineChange(
                                Instant.fromEpochMilliseconds(selected)
                                    .toLocalDateTime(TimeZone.UTC)
                                    .toString()
                            )
                            showDatePicker.value = false
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
