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
 * LoginViewModel - handles login logic
 */
class LoginViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    /**
     * Perform login with email and password using Supabase authentication
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

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

                if (!isValidEmail(email)) {
                    _errorMessage.value = "Please enter a valid email address"
                    return@launch
                }

                // Authenticate with Supabase
                val userInfo = authService.signInWithEmail(email, password)

                // Retrieve and store authentication token
                val token = authService.getAccessToken()

                if (token != null) {
                    _userId.value = userInfo.id
                    _accessToken.value = token
                    _isAuthenticated.value = true
                } else {
                    _errorMessage.value = "Failed to retrieve authentication token"
                }

            } catch (e: RestException) {
                // Handle Supabase-specific errors
                _errorMessage.value = when {
                    e.message?.contains("Invalid login credentials") == true ->
                        "Invalid email or password"
                    e.message?.contains("Email not confirmed") == true ->
                        "Please confirm your email address"
                    else -> e.message ?: "Authentication failed"
                }
            } catch (e: IOException) {
                // Handle network errors
                _errorMessage.value = "Network error. Please check your connection and try again"
            } catch (e: Exception) {
                // Handle other errors
                _errorMessage.value = e.message ?: "Login failed. Please try again"
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
     * Sign out the current user
     */
    suspend fun signOut() {
        try {
            authService.signOut()
            _isAuthenticated.value = false
            _userId.value = null
            _accessToken.value = null
            _errorMessage.value = null
        } catch (e: Exception) {
            _errorMessage.value = "Failed to sign out: ${e.message}"
        }
    }

    /**
     * Reset authentication state (useful when returning to login screen)
     */
    fun resetAuthState() {
        _isAuthenticated.value = false
        _userId.value = null
        _accessToken.value = null
        _errorMessage.value = null
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

