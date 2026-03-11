package com.example.taskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.exceptions.UserNotAuthenticatedException
import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing the result of a task operation
 */
sealed class TaskOperationResult {
    object Idle : TaskOperationResult()
    object InProgress : TaskOperationResult()
    data class Success(val message: String) : TaskOperationResult()
    data class Error(val message: String) : TaskOperationResult()
}

/**
 * ViewModel for managing task-related state and operations
 */
class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository

    // State for the list of tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // State for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // State for non-critical error messages (shown in Snackbar)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // State for operation results (add/update/delete)
    private val _operationResult = MutableStateFlow<TaskOperationResult>(TaskOperationResult.Idle)
    val operationResult: StateFlow<TaskOperationResult> = _operationResult.asStateFlow()


    /**
     * Load all tasks from Supabase for the current user
     */
    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _tasks.value = taskRepository.getTasksForCurrentUser()
                _error.value = null
            } catch (e: UserNotAuthenticatedException) {
                _error.value = "Authentication error. Please restart the app."
                _tasks.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to load tasks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a new task to Supabase
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            _operationResult.value = TaskOperationResult.InProgress
            _isLoading.value = true
            try {
                val createdTask = taskRepository.addTask(task)
                _tasks.value = _tasks.value + createdTask
                _error.value = null
                _operationResult.value = TaskOperationResult.Success("Task added successfully")
            } catch (e: UserNotAuthenticatedException) {
                val errorMsg = "Authentication error. Please restart the app."
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Failed to add task: ${e.message}"
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update an existing task in Supabase
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            _operationResult.value = TaskOperationResult.InProgress
            _isLoading.value = true
            try {
                val updatedTask = taskRepository.updateTask(task)
                _tasks.value = _tasks.value.map { if (it.id == updatedTask.id) updatedTask else it }
                _error.value = null
                _operationResult.value = TaskOperationResult.Success("Task updated successfully")
            } catch (e: UserNotAuthenticatedException) {
                val errorMsg = "Authentication error. Please restart the app."
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Failed to update task: ${e.message}"
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a task from Supabase
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _operationResult.value = TaskOperationResult.InProgress
            _isLoading.value = true
            try {
                taskRepository.deleteTask(taskId)
                _tasks.value = _tasks.value.filter { it.id != taskId }
                _error.value = null
                _operationResult.value = TaskOperationResult.Success("Task deleted successfully")
            } catch (e: UserNotAuthenticatedException) {
                val errorMsg = "Authentication error. Please restart the app."
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Failed to delete task: ${e.message}"
                _error.value = errorMsg
                _operationResult.value = TaskOperationResult.Error(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle task completion status with optimistic UI update
     * This method updates the UI immediately and syncs to Supabase in the background
     */
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            try {
                // Step 1: Get current task state
                val task = _tasks.value.find { it.id == taskId }
                if (task == null) {
                    _snackbarMessage.value = "Task not found"
                    return@launch
                }

                // Step 2: Optimistically update UI immediately
                val newCompletionState = !task.isCompleted
                _tasks.value = _tasks.value.map {
                    if (it.id == taskId) it.copy(isCompleted = newCompletionState) else it
                }

                // Step 3: Persist to database in background (no loading state)
                taskRepository.toggleTaskCompletion(taskId, newCompletionState)

            } catch (e: UserNotAuthenticatedException) {
                // Revert optimistic update
                _snackbarMessage.value = "Authentication error. Please sign in again."
                loadTasks() // Reload to get correct state from server
            } catch (e: Exception) {
                // Revert optimistic update
                _snackbarMessage.value = "Failed to update task. Please try again."
                loadTasks() // Reload to get correct state from server
            }
        }
    }

    /**
     * Get a task by ID
     */
    fun getTaskById(taskId: String): Task? {
        return _tasks.value.find { it.id == taskId }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear snackbar message
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    /**
     * Reset operation result state
     */
    fun resetOperationResult() {
        _operationResult.value = TaskOperationResult.Idle
    }
}

