package com.example.taskmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.taskmaster.data.preferences.ThemeMode
import com.example.taskmaster.navigation.NavGraph
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.viewmodel.TaskViewModel
import com.example.taskmaster.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            // Calculate darkTheme based on current themeMode
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme
            }

            TaskMasterTheme(darkTheme = darkTheme) {
                TaskMasterApp()
            }
        }
    }
}

@Composable
fun TaskMasterApp() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavGraph(navController = navController)
}