package com.example.taskmaster.data.service

import com.example.taskmaster.data.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

class AuthService {

    private val supabase = SupabaseClient.client

    suspend fun signInWithEmail(email: String, password: String): UserInfo {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        return supabase.auth.currentUserOrNull()
            ?: throw Exception("Login failed: User not authenticated")
    }

    suspend fun signUpWithEmail(email: String, password: String): UserInfo {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        return supabase.auth.currentUserOrNull()
            ?: throw Exception("Registration failed: User not created")
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun getCurrentUser(): UserInfo? {
        return supabase.auth.currentUserOrNull()
    }

    fun getAccessToken(): String? {
        return supabase.auth.currentAccessTokenOrNull()
    }

    fun isAuthenticated(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}

