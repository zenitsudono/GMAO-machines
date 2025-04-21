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
        val user = result.user
        
        if (user != null) {
            // Send email verification
            user.sendEmailVerification().await()
            
            // Update display name (optional)
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()
            
            user.updateProfile(profileUpdates).await()
            
            return true
        }
        
        return false
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

    /**
     * Checks if the current user's email is verified
     * @return Boolean indicating if email is verified
     */
    suspend fun isEmailVerified(): Boolean {
        val currentUser = auth.currentUser ?: return false
        
        // Reload user to get the most recent status
        currentUser.reload().await()
        
        return currentUser.isEmailVerified
    }
    
    /**
     * Resends the verification email to the current user
     * @return Boolean indicating if the email was sent successfully
     */
    suspend fun resendVerificationEmail(): Boolean {
        val currentUser = auth.currentUser ?: throw Exception("No user is currently signed in")
        
        return try {
            currentUser.sendEmailVerification().await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error sending verification email: ${e.message}", e)
            throw Exception("Failed to send verification email: ${e.message}")
        }
    }

    /**
     * Gets user information by email address for biometric login
     * This is used when authenticating with biometrics, where we need to fetch the user
     * based on a saved email address.
     * 
     * @param email The email address to look up
     * @return User object if found, null otherwise
     */
    suspend fun getUserByEmail(email: String): User? {
        if (email.isBlank()) {
            Log.e("AuthRepository", "Email is blank")
            return null
        }
        
        try {
            // In a real app, this would query the database to get user info
            // For now, we'll just create a basic user object with the email
            // Typically this would be integrated with Firestore or another database
            
            // Check if there are any users with this email in your authentication system
            val users = auth.fetchSignInMethodsForEmail(email).await()
            
            if (users.signInMethods.isNullOrEmpty()) {
                Log.d("AuthRepository", "No user found with email $email")
                return null
            }
            
            // Create a user object with the minimum required info
            return User(
                email = email,
                firstName = "",  // In a real app, fetch this from your database
                lastName = ""    // In a real app, fetch this from your database
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting user by email: ${e.message}", e)
            return null
        }
    }
}