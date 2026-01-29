package com.example.taskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task-related state and operations
 */
class TaskViewModel : ViewModel() {

    // State for the list of tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // State for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Load tasks when ViewModel is created
        loadTasks()
    }

    /**
     * Load all tasks (mock data for now, will connect to Supabase later)
     */
    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual Supabase call
                // For now, using mock data
                _tasks.value = getMockTasks()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load tasks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a new task (mock implementation for now)
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual Supabase call
                _tasks.value = _tasks.value + task.copy(id = (_tasks.value.maxOfOrNull { it.id ?: 0 } ?: 0) + 1)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update an existing task (mock implementation for now)
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual Supabase call
                _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a task (mock implementation for now)
     */
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual Supabase call
                _tasks.value = _tasks.value.filter { it.id != taskId }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to delete task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual Supabase call
                _tasks.value = _tasks.value.map {
                    if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
                }
                _error.value = null
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
    fun getTaskById(taskId: Int): Task? {
        return _tasks.value.find { it.id == taskId }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Mock data for testing
     */
    private fun getMockTasks(): List<Task> {
        return listOf(
            Task(
                id = 1,
                title = "Complete project proposal",
                description = "Finish writing the TaskMaster project proposal document",
                priority = TaskPriority.HIGH,
                deadline = "2026-02-05T17:00:00",
                isCompleted = false
            ),
            Task(
                id = 2,
                title = "Buy groceries",
                description = "Milk, eggs, bread, vegetables",
                priority = TaskPriority.MEDIUM,
                deadline = "2026-01-30T18:00:00",
                isCompleted = false
            ),
            Task(
                id = 3,
                title = "Call dentist",
                description = "Schedule appointment for checkup",
                priority = TaskPriority.LOW,
                deadline = null,
                isCompleted = true
            )
        )
    }
}

