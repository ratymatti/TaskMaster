package com.example.taskmaster.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskmaster.ui.screens.AddTaskScreen
import com.example.taskmaster.ui.screens.EditTaskScreen
import com.example.taskmaster.ui.screens.LoginScreen
import com.example.taskmaster.ui.screens.RegisterScreen
import com.example.taskmaster.ui.screens.TaskListScreen
import com.example.taskmaster.viewmodel.LoginViewModel
import com.example.taskmaster.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

/**
 * Navigation graph for the TaskMaster app
 */
@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: TaskViewModel = viewModel() // Shared ViewModel instance
    val loginViewModel: LoginViewModel = viewModel() // Shared LoginViewModel instance
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // Login Screen
        composable(route = Routes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.TaskList.route) {
                        // Clear the login screen from back stack
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        // Register Screen
        composable(route = Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack() // Go back to login
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Go back to login
                }
            )
        }

        // Task List Screen
        composable(route = Routes.TaskList.route) {
            TaskListScreen(
                viewModel = viewModel, // Pass shared ViewModel
                onAddTask = {
                    navController.navigate(Routes.AddTask.route)
                },
                onEditTask = { taskId ->
                    navController.navigate(Routes.EditTask.createRoute(taskId))
                },
                onSignOut = {
                    coroutineScope.launch {
                        loginViewModel.signOut()
                        navController.navigate(Routes.Login.route) {
                            // Clear the entire back stack
                            popUpTo(0) {
                                inclusive = true
                            }
                            // Avoid multiple copies of login screen
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // Add Task Screen
        composable(route = Routes.AddTask.route) {
            AddTaskScreen(
                viewModel = viewModel, // Pass shared ViewModel
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
                viewModel = viewModel, // Pass shared ViewModel
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
