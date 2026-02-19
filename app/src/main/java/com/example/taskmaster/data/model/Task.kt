package com.example.taskmaster.data.model

import kotlinx.serialization.Serializable

/**
 * Task data model representing a task in the TaskMaster app
 * Maps to the 'tasks' table in Supabase
 */
@Serializable
data class Task(
    val id: String? = null,        // UUID from Supabase
    val userId: String? = null,    // User ID for association
    val title: String,
    val description: String? = null,
    val priority: TaskPriority,
    val deadline: String? = null, // ISO 8601 format string
    val isCompleted: Boolean = false,
    val createdAt: String? = null, // ISO 8601 format string
    val updatedAt: String? = null  // ISO 8601 format string
)

/**
 * Task priority levels
 */
@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

