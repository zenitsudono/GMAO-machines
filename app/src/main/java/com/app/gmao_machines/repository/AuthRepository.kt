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
}