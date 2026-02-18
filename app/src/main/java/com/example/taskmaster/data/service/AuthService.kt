package com.example.taskmaster.data.service

import com.example.taskmaster.data.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Authentication Service
 * Handles all authentication operations with Supabase
 */
class AuthService {

    private val supabase = SupabaseClient.client

    /**
     * Sign in with email and password
     * @param email User's email
     * @param password User's password
     * @return UserInfo on success
     * @throws Exception on failure
     */
    suspend fun signInWithEmail(email: String, password: String): UserInfo {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        return supabase.auth.currentUserOrNull()
            ?: throw Exception("Login failed: User not authenticated")
    }

    /**
     * Sign up with email and password
     * @param email User's email
     * @param password User's password
     * @return UserInfo on success
     * @throws Exception on failure
     */
    suspend fun signUpWithEmail(email: String, password: String): UserInfo {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        return supabase.auth.currentUserOrNull()
            ?: throw Exception("Registration failed: User not created")
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut() {
        supabase.auth.signOut()
    }

    /**
     * Get the current user
     * @return UserInfo if authenticated, null otherwise
     */
    fun getCurrentUser(): UserInfo? {
        return supabase.auth.currentUserOrNull()
    }

    /**
     * Get the current session access token
     * @return Access token if authenticated, null otherwise
     */
    fun getAccessToken(): String? {
        return supabase.auth.currentAccessTokenOrNull()
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated, false otherwise
     */
    fun isAuthenticated(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}

