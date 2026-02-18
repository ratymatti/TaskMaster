package com.example.taskmaster.navigation

/**
 * Navigation routes for the TaskMaster app
 */
sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object TaskList : Routes("task_list")
    data object AddTask : Routes("add_task")
    data object EditTask : Routes("edit_task/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    }
}


