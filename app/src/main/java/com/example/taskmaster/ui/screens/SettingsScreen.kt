package com.example.taskmaster.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.data.preferences.ThemeMode
import com.example.taskmaster.viewmodel.ThemeViewModel

/**
 * Settings screen for app configuration including theme selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val currentThemeMode by themeViewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Appearance Section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Light Mode Option
                    ThemeOptionItem(
                        title = "Light Mode",
                        description = "Always use light theme",
                        selected = currentThemeMode == ThemeMode.LIGHT,
                        onClick = { themeViewModel.updateThemeMode(ThemeMode.LIGHT) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Dark Mode Option
                    ThemeOptionItem(
                        title = "Dark Mode",
                        description = "Always use dark theme",
                        selected = currentThemeMode == ThemeMode.DARK,
                        onClick = { themeViewModel.updateThemeMode(ThemeMode.DARK) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // System Default Option
                    ThemeOptionItem(
                        title = "System Default",
                        description = "Follow system theme setting",
                        selected = currentThemeMode == ThemeMode.SYSTEM,
                        onClick = { themeViewModel.updateThemeMode(ThemeMode.SYSTEM) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info text
            Text(
                text = "Changes apply immediately",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Individual theme option item with radio button
 */
@Composable
private fun ThemeOptionItem(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

