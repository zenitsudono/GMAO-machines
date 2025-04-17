package com.app.gmao_machines.repository

import com.app.gmao_machines.data.User
import com.google.firebase.auth.FirebaseAuth
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
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        val firebaseUser = authResult.user ?: throw Exception("Authentication failed")

        return User(
            email = firebaseUser.email ?: "",
            firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
            lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: ""
        )
    }
}