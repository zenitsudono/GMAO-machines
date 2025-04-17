package com.app.gmao_machines.repository

import com.app.gmao_machines.data.User
import kotlinx.coroutines.delay

class AuthRepository {
    // These methods would actually connect to your authentication service
    // They're mocked here for demonstration

    suspend fun registerUser(firstName: String, lastName: String, email: String, password: String): Boolean {
        // Simulate network delay
        delay(1000)
        // In a real app, this would call your authentication API
        return true
    }

    suspend fun signInUser(email: String, password: String): User {
        // Simulate network delay
        delay(1000)
        // In a real app, this would validate credentials with your backend
        return User(email = email, firstName = "Test", lastName = "User")
    }

    suspend fun signInWithGoogle(idToken: String): User {
        // Simulate network delay
        delay(1000)
        // In a real app, this would verify the token with your backend
        return User(email = "google@example.com", firstName = "Google", lastName = "User")
    }
}