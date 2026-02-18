package com.example.taskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.service.AuthService
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * RegisterViewModel - handles user registration logic
 */
class RegisterViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isRegistrationSuccessful = MutableStateFlow(false)
    val isRegistrationSuccessful: StateFlow<Boolean> = _isRegistrationSuccessful.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    /**
     * Perform registration with email and password using Supabase authentication
     */
    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isRegistrationSuccessful.value = false

            try {
                // Validate input
                if (email.isBlank()) {
                    _errorMessage.value = "Email is required"
                    return@launch
                }

                if (password.isBlank()) {
                    _errorMessage.value = "Password is required"
                    return@launch
                }

                if (confirmPassword.isBlank()) {
                    _errorMessage.value = "Please confirm your password"
                    return@launch
                }

                if (!isValidEmail(email)) {
                    _errorMessage.value = "Please enter a valid email address"
                    return@launch
                }

                if (password.length < 8) {
                    _errorMessage.value = "Password must be at least 8 characters"
                    return@launch
                }

                if (password != confirmPassword) {
                    _errorMessage.value = "Passwords do not match"
                    return@launch
                }

                // Register with Supabase
                val userInfo = authService.signUpWithEmail(email, password)

                // Store user ID
                _userId.value = userInfo.id
                _isRegistrationSuccessful.value = true

            } catch (e: RestException) {
                // Handle Supabase-specific errors
                _errorMessage.value = when {
                    e.message?.contains("User already registered") == true ||
                    e.message?.contains("already been registered") == true ->
                        "An account with this email already exists"
                    e.message?.contains("Password should be at least") == true ->
                        "Password must be at least 8 characters"
                    e.message?.contains("Unable to validate email") == true ||
                    e.message?.contains("invalid format") == true ->
                        "Please enter a valid email address"
                    else -> e.message ?: "Registration failed"
                }
            } catch (e: IOException) {
                // Handle network errors
                _errorMessage.value = "Network error. Please check your connection and try again"
            } catch (e: Exception) {
                // Handle other errors
                _errorMessage.value = e.message ?: "Registration failed. Please try again"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Reset registration success state
     */
    fun resetRegistrationState() {
        _isRegistrationSuccessful.value = false
    }
}

