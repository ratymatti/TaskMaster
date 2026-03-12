package com.example.taskmaster.ui.screens.components

import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.model.TaskPriority

/**
 * Encapsulates all form field state for task creation and editing.
 */
data class TaskFormState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val deadline: String = ""
) {
    fun isValid(): Boolean = title.isNotBlank()

    fun toTask(existingTask: Task? = null): Task {
        return Task(
            id = existingTask?.id,
            userId = existingTask?.userId,
            title = title,
            description = description.ifBlank { null },
            priority = priority,
            deadline = deadline.ifBlank { null },
            isCompleted = existingTask?.isCompleted ?: false,
            createdAt = existingTask?.createdAt,
            updatedAt = existingTask?.updatedAt
        )
    }

    companion object {
        fun fromTask(task: Task): TaskFormState {
            return TaskFormState(
                title = task.title,
                description = task.description ?: "",
                priority = task.priority,
                deadline = task.deadline ?: ""
            )
        }
    }
}

