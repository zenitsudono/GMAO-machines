package com.app.gmao_machines.repository

import android.util.Log
import com.app.gmao_machines.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user != null
    }

    suspend fun signInUser(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("User not found")

        return User(
            email = firebaseUser.email ?: "",
            firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
            lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: ""
        )
    }

    suspend fun signInWithGoogle(idToken: String): User {
        Log.d("AuthRepository", "Starting Google sign in with Firebase")
        try {
            if (idToken.isBlank()) {
                Log.e("AuthRepository", "ID token is blank")
                throw Exception("Invalid authentication token")
            }
            
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d("AuthRepository", "Created credential from token")
            
            // Add a retry mechanism for network issues
            var attempts = 0
            var authResult = try {
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                if (attempts < 2) {
                    attempts++
                    Log.w("AuthRepository", "Retrying Firebase auth, attempt $attempts", e)
                    auth.signInWithCredential(credential).await()
                } else {
                    throw e
                }
            }
            
            Log.d("AuthRepository", "Firebase auth completed")
            
            val firebaseUser = authResult.user ?: throw Exception("Authentication failed: no user returned")
            Log.d("AuthRepository", "Got Firebase user: ${firebaseUser.email}, UID: ${firebaseUser.uid}")
            
            // Verify the account exists
            if (firebaseUser.email.isNullOrBlank()) {
                Log.w("AuthRepository", "User has no email address, this might cause issues")
            }
            
            return User(
                email = firebaseUser.email ?: "",
                firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
                lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: ""
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error during Firebase Google sign-in: ${e.message}", e)
            throw Exception("Google authentication failed: ${e.message}")
        }
    }
    
    /**
     * Sends a password reset email to the specified email address
     * @param email The email address to send the password reset link to
     * @return Boolean indicating if the reset email was sent successfully
     * @throws Exception if there's an error sending the email
     */
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        Log.d("AuthRepository", "Sending password reset email to $email")
        
        if (email.isBlank()) {
            Log.e("AuthRepository", "Email is blank")
            throw Exception("Email address cannot be empty")
        }
        
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d("AuthRepository", "Password reset email sent successfully")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error sending password reset email: ${e.message}", e)
            throw Exception("Failed to send password reset email: ${e.message}")
        }
    }
}