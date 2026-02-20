package com.example.taskmaster.data.repository

import com.example.taskmaster.data.SupabaseClient
import com.example.taskmaster.data.exceptions.DatabaseException
import com.example.taskmaster.data.exceptions.NetworkException
import com.example.taskmaster.data.exceptions.UserNotAuthenticatedException
import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.model.TaskDTO
import com.example.taskmaster.data.model.toTask
import com.example.taskmaster.data.model.toTaskDTO
import com.example.taskmaster.data.service.AuthService
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerializationException
import java.io.IOException

/**
 * Repository for Task operations with Supabase
 * Handles all database interactions for tasks
 */
object TaskRepository {
    private val supabase = SupabaseClient.client
    private val authService = AuthService()

    /**
     * Get all tasks for the current authenticated user
     * @return List of tasks for the current user
     * @throws UserNotAuthenticatedException if user is not authenticated
     * @throws NetworkException if network error occurs
     * @throws DatabaseException if database query fails
     */
    suspend fun getTasksForCurrentUser(): List<Task> {
        try {
            // Get current user
            val currentUser = authService.getCurrentUser()
                ?: throw UserNotAuthenticatedException("User is not authenticated")

            // Get user ID
            val userId = currentUser.id
            android.util.Log.d("TaskRepository", "Loading tasks for user: $userId")

            // Query tasks from Supabase filtered by user_id
            val taskDTOs = supabase
                .from("Tasks")
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<TaskDTO>()

            android.util.Log.d("TaskRepository", "Successfully loaded ${taskDTOs.size} tasks")

            // Convert DTOs to Task models
            return taskDTOs.map { it.toTask() }

        } catch (e: UserNotAuthenticatedException) {
            android.util.Log.e("TaskRepository", "User not authenticated", e)
            throw e
        } catch (e: IOException) {
            android.util.Log.e("TaskRepository", "Network error loading tasks", e)
            throw NetworkException("Network error while loading tasks", e)
        } catch (e: SerializationException) {
            android.util.Log.e("TaskRepository", "Serialization error: ${e.message}", e)
            throw DatabaseException("Failed to parse tasks from database: ${e.message}", e)
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Error loading tasks: ${e.message}", e)
            throw DatabaseException("Failed to load tasks: ${e.message}", e)
        }
    }

    /**
     * Add a new task for the current user
     * @param task Task to add
     * @return The created task with id
     * @throws UserNotAuthenticatedException if user is not authenticated
     */
    suspend fun addTask(task: Task): Task {
        try {
            val currentUser = authService.getCurrentUser()
                ?: throw UserNotAuthenticatedException("User is not authenticated")

            val userId = currentUser.id

            // Create TaskDTO for insertion
            val taskDTOToInsert = TaskDTO(
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                isCompleted = task.isCompleted,
                deadline = task.deadline,
                userId = userId
            )

            val createdTaskDTO = supabase
                .from("Tasks")
                .insert(taskDTOToInsert) {
                    select()
                }
                .decodeSingle<TaskDTO>()

            return createdTaskDTO.toTask()

        } catch (e: UserNotAuthenticatedException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException("Network error while adding task", e)
        } catch (e: Exception) {
            throw DatabaseException("Failed to add task: ${e.message}", e)
        }
    }

    /**
     * Update an existing task
     * @param task Task to update
     * @return The updated task
     * @throws UserNotAuthenticatedException if user is not authenticated
     */
    suspend fun updateTask(task: Task): Task {
        try {
            val currentUser = authService.getCurrentUser()
                ?: throw UserNotAuthenticatedException("User is not authenticated")

            val userId = currentUser.id

            // Create TaskDTO for update
            val taskDTOToUpdate = TaskDTO(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                isCompleted = task.isCompleted,
                deadline = task.deadline,
                userId = userId
            )

            val updatedTaskDTO = supabase
                .from("Tasks")
                .update(taskDTOToUpdate) {
                    select()
                    filter {
                        eq("id", task.id ?: "")
                    }
                }
                .decodeSingle<TaskDTO>()

            return updatedTaskDTO.toTask()

        } catch (e: UserNotAuthenticatedException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException("Network error while updating task", e)
        } catch (e: Exception) {
            throw DatabaseException("Failed to update task: ${e.message}", e)
        }
    }

    /**
     * Delete a task by ID
     * @param taskId ID of the task to delete
     * @throws UserNotAuthenticatedException if user is not authenticated
     */
    suspend fun deleteTask(taskId: String) {
        try {
            // Verify user is authenticated
            authService.getCurrentUser()
                ?: throw UserNotAuthenticatedException("User is not authenticated")

            supabase
                .from("Tasks")
                .delete {
                    filter {
                        eq("id", taskId)
                    }
                }

        } catch (e: UserNotAuthenticatedException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException("Network error while deleting task", e)
        } catch (e: Exception) {
            throw DatabaseException("Failed to delete task: ${e.message}", e)
        }
    }

    /**
     * Toggle task completion status
     * @param taskId ID of the task to toggle
     * @param isCompleted New completion status
     * @return The updated task
     * @throws UserNotAuthenticatedException if user is not authenticated
     */
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean): Task {
        try {
            // Verify user is authenticated
            authService.getCurrentUser()
                ?: throw UserNotAuthenticatedException("User is not authenticated")

            val updatedTaskDTO = supabase
                .from("Tasks")
                .update({
                    set("is_completed", isCompleted)
                }) {
                    select()
                    filter {
                        eq("id", taskId)
                    }
                }
                .decodeSingle<TaskDTO>()

            return updatedTaskDTO.toTask()

        } catch (e: UserNotAuthenticatedException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException("Network error while updating task", e)
        } catch (e: Exception) {
            throw DatabaseException("Failed to update task: ${e.message}", e)
        }
    }
}

