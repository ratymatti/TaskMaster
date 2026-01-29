package com.example.taskmaster.navigation
}
    }
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    data object EditTask : Routes("edit_task/{taskId}") {
    data object AddTask : Routes("add_task")
    data object TaskList : Routes("task_list")
sealed class Routes(val route: String) {
 */
 * Navigation routes for the TaskMaster app
/**


