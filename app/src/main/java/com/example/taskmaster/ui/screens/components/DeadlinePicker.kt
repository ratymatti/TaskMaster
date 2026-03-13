package com.example.taskmaster.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.*

fun formatDeadlineForDisplay(isoString: String): String? {
    if (isoString.isBlank()) return null
    return runCatching {
        val dt = LocalDateTime.parse(isoString)
        val monthAbbr = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val hour = dt.hour.toString().padStart(2, '0')
        "$monthAbbr ${dt.dayOfMonth}, ${dt.year}  $hour:00"
    }.getOrNull()
}

fun buildIsoDeadline(dateMillis: Long, hour: Int): String {
    val date = Instant.fromEpochMilliseconds(dateMillis)
        .toLocalDateTime(TimeZone.UTC)
        .date
    val time = LocalTime(hour, 0, 0)
    return LocalDateTime(date, time).toString()
}

@Composable
fun HourPicker(
    selectedHour: Int,
    modifier: Modifier = Modifier,
    onHourSelected: (Int) -> Unit
) {
    val hours = remember { (0..23).toList() }
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier.heightIn(max = 240.dp)
    ) {
        items(hours) { hour ->
            val isSelected = hour == selectedHour
            Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clickable { onHourSelected(hour) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinePicker(
    deadline: String,
    onDeadlineChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val pendingDateMillis = remember { mutableStateOf<Long?>(null) }

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

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val pendingHour = remember(deadline) { mutableIntStateOf(initialHour) }

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
    }

    // ── Step 1: Date picker ──────────────────────────────────────────────────
    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            pendingDateMillis.value = selected
                            showDatePicker.value = false
                            showTimePicker.value = true
                        }
                    }
                ) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Step 2: Hour picker ──────────────────────────────────────────────────
    if (showTimePicker.value) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker.value = false
                pendingDateMillis.value = null
            },
            title = { Text("Select Hour") },
            text = {
                HourPicker(
                    selectedHour = pendingHour.intValue,
                    onHourSelected = { pendingHour.intValue = it }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateMs = pendingDateMillis.value
                        if (dateMs != null) {
                            onDeadlineChange(buildIsoDeadline(dateMs, pendingHour.intValue))
                        }
                        showTimePicker.value = false
                        pendingDateMillis.value = null
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker.value = false
                        pendingDateMillis.value = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}
