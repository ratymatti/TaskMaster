package com.example.taskmaster.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Data Transfer Object for Task
 * Maps Supabase snake_case fields to Kotlin camelCase
 */
@Serializable
data class TaskDTO(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val priority: String,
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    val deadline: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Extension function to convert TaskDTO to Task
 */
fun TaskDTO.toTask(): Task {
    return Task(
        id = id,
        userId = userId,
        title = title,
        description = description,
        priority = try {
            TaskPriority.valueOf(priority.uppercase())
        } catch (e: IllegalArgumentException) {
            // Default to MEDIUM if priority value is invalid
            TaskPriority.MEDIUM
        },
        deadline = deadline,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Extension function to convert Task to TaskDTO
 */
fun Task.toTaskDTO(userId: String): TaskDTO {
    return TaskDTO(
        id = id,
        userId = userId,
        title = title,
        description = description,
        priority = priority.name,
        isCompleted = isCompleted,
        deadline = deadline,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

