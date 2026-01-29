package com.example.taskmaster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskmaster.ui.screens.AddTaskScreen
import com.example.taskmaster.ui.screens.EditTaskScreen
import com.example.taskmaster.ui.screens.TaskListScreen

/**
 * Navigation graph for the TaskMaster app
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.TaskList.route
    ) {
        // Task List Screen
        composable(route = Routes.TaskList.route) {
            TaskListScreen(
                onAddTask = {
                    navController.navigate(Routes.AddTask.route)
                },
                onEditTask = { taskId ->
                    navController.navigate(Routes.EditTask.createRoute(taskId))
                }
            )
        }

        // Add Task Screen
        composable(route = Routes.AddTask.route) {
            AddTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Task Screen
        composable(
            route = Routes.EditTask.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry: NavBackStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

