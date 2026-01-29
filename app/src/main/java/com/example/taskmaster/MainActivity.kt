package com.example.taskmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.taskmaster.navigation.NavGraph
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskMasterTheme {
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