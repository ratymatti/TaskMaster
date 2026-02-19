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
            _isLoading.value = true
            try {
                val createdTask = taskRepository.addTask(task)
                _tasks.value = _tasks.value + createdTask
                _error.value = null
            } catch (e: UserNotAuthenticatedException) {
                _error.value = "Authentication error. Please restart the app."
            } catch (e: Exception) {
                _error.value = "Failed to add task: ${e.message}"
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
            _isLoading.value = true
            try {
                val updatedTask = taskRepository.updateTask(task)
                _tasks.value = _tasks.value.map { if (it.id == updatedTask.id) updatedTask else it }
                _error.value = null
            } catch (e: UserNotAuthenticatedException) {
                _error.value = "Authentication error. Please restart the app."
            } catch (e: Exception) {
                _error.value = "Failed to update task: ${e.message}"
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
            _isLoading.value = true
            try {
                taskRepository.deleteTask(taskId)
                _tasks.value = _tasks.value.filter { it.id != taskId }
                _error.value = null
            } catch (e: UserNotAuthenticatedException) {
                _error.value = "Authentication error. Please restart the app."
            } catch (e: Exception) {
                _error.value = "Failed to delete task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle task completion status in Supabase
     */
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val task = _tasks.value.find { it.id == taskId }
                if (task != null) {
                    val updatedTask = taskRepository.toggleTaskCompletion(taskId, !task.isCompleted)
                    _tasks.value = _tasks.value.map {
                        if (it.id == taskId) updatedTask else it
                    }
                    _error.value = null
                }
            } catch (e: UserNotAuthenticatedException) {
                _error.value = "Authentication error. Please restart the app."
            } catch (e: Exception) {
                _error.value = "Failed to update task: ${e.message}"
            } finally {
                _isLoading.value = false
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
}

