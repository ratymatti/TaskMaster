package com.example.taskmaster.ui.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Reusable Password Visibility Toggle Icon
 *
 * @param passwordVisible Whether the password is currently visible
 * @param onToggle Callback when the visibility is toggled
 */
@Composable
fun PasswordVisibilityToggle(
    passwordVisible: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Text(
            text = if (passwordVisible) "👁️" else "👁️‍🗨️",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
