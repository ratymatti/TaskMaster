package com.example.taskmaster.data.exceptions

/**
 * Exception thrown when a user is not authenticated
 */
class UserNotAuthenticatedException(
    message: String = "User is not authenticated"
) : Exception(message)

/**
 * Exception thrown when a network error occurs
 */
class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when a database operation fails
 */
class DatabaseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

