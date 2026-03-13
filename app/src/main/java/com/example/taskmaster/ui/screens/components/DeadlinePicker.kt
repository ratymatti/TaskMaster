package com.example.taskmaster.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.*

/**
 * Parses an ISO 8601 deadline string and returns a human-readable label,
 * e.g. "Feb 15, 2026  17:00". Returns null if the string is blank or unparseable.
 */
fun formatDeadlineForDisplay(isoString: String): String? {
    if (isoString.isBlank()) return null
    return runCatching {
        val dt = LocalDateTime.parse(isoString)
        val monthAbbr = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val hour = dt.hour.toString().padStart(2, '0')
        val minute = dt.minute.toString().padStart(2, '0')
        "$monthAbbr ${dt.dayOfMonth}, ${dt.year}  $hour:$minute"
    }.getOrNull()
}

/**
 * Combines a date (epoch millis from DatePickerState) and a TimePickerState
 * into an ISO 8601 string "yyyy-MM-ddTHH:mm:ss".
 */
@OptIn(ExperimentalMaterial3Api::class)
fun buildIsoDeadline(dateMillis: Long, timeState: TimePickerState): String {
    val date = Instant.fromEpochMilliseconds(dateMillis)
        .toLocalDateTime(TimeZone.UTC)
        .date
    val time = LocalTime(timeState.hour, timeState.minute, 0)
    return LocalDateTime(date, time).toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinePicker(
    deadline: String,
    onDeadlineChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateMillis by remember { mutableStateOf<Long?>(null) }

    // Pre-populate the pickers from the existing deadline when editing a task
    val initialMillis: Long = remember(deadline) {
        if (deadline.isBlank()) System.currentTimeMillis()
        else runCatching {
            LocalDateTime.parse(deadline)
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        }.getOrDefault(System.currentTimeMillis())
    }

    val initialHour: Int = remember(deadline) {
        if (deadline.isBlank()) 9
        else runCatching { LocalDateTime.parse(deadline).hour }.getOrDefault(9)
    }

    val initialMinute: Int = remember(deadline) {
        if (deadline.isBlank()) 0
        else runCatching { LocalDateTime.parse(deadline).minute }.getOrDefault(0)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val displayText = formatDeadlineForDisplay(deadline) ?: ""

    // Use enabled=false on the OutlinedTextField to suppress the keyboard,
    // but override the disabled colors so it looks like an active field.
    // A transparent Box overlay captures taps and opens the date picker.
    Box(modifier = modifier) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            label = { Text("Deadline (Optional)") },
            placeholder = { Text("Tap to set a deadline") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor         = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor       = MaterialTheme.colorScheme.outline,
                disabledLabelColor        = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor  = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            trailingIcon = {
                if (deadline.isNotBlank()) {
                    IconButton(onClick = { onDeadlineChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear deadline"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick deadline"
                    )
                }
            }
        )
        // Transparent overlay that captures taps without stealing text focus
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
    }

    // ── Step 1: Date picker ──────────────────────────────────────────────────
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            pendingDateMillis = selected
                            showDatePicker = false
                            showTimePicker = true   // immediately open time picker
                        }
                    }
                ) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Step 2: Time picker ──────────────────────────────────────────────────
    // Material3 has no standalone TimePickerDialog composable;
    // the standard approach is an AlertDialog wrapping TimePicker.
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
                pendingDateMillis = null   // user cancelled mid-flow — no update
            },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateMs = pendingDateMillis
                        if (dateMs != null) {
                            onDeadlineChange(buildIsoDeadline(dateMs, timePickerState))
                        }
                        showTimePicker = false
                        pendingDateMillis = null
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        pendingDateMillis = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}

