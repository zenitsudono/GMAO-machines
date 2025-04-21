package com.app.gmao_machines.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.app.gmao_machines.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserInfo(
    val displayName: String = "User",
    val email: String = "",
    val machineCount: Int = 0,
    val maintenanceCount: Int = 0,
    val reportCount: Int = 0
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var sharedPreferences: android.content.SharedPreferences

    // StateFlow for user information
    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()
    
    init {
        loadUserInfo()
        loadUserStats()
    }

    // Initialize shared preferences
    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences("gmao_machines_prefs", Context.MODE_PRIVATE)
        }
    }
    
    private fun loadUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName ?: 
                if (currentUser.email?.contains("@") == true) {
                    currentUser.email?.split("@")[0]
                } else "User"
                
            _userInfo.value = _userInfo.value.copy(
                displayName = displayName.toString(),
                email = currentUser.email ?: ""
            )
        }
    }
    
    // In a real app, this would load from Firestore or another database
    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                // Simulating data fetch from a database
                // In a real app, replace with actual database queries
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    // Example: Replace with actual database calls
                    // val machines = firestoreDb.collection("machines").whereEqualTo("userId", userId).get().await()
                    // val maintenance = firestoreDb.collection("maintenance").whereEqualTo("userId", userId).get().await()
                    // val reports = firestoreDb.collection("reports").whereEqualTo("userId", userId).get().await()
                    
                    // For now, using dummy data
                    _userInfo.value = _userInfo.value.copy(
                        machineCount = 28,
                        maintenanceCount = 156,
                        reportCount = 43
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading user stats", e)
            }
        }
    }

    fun signOut(context: Context, onSignOutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Sign out from Firebase
                auth.signOut()

                // Sign out from Google
                try {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut().await()
                } catch (e: Exception) {
                    // Handle Google sign out error but continue
                    e.printStackTrace()
                }

                onSignOutComplete()
            } catch (e: Exception) {
                // Handle sign out error if needed
                e.printStackTrace()
                onSignOutComplete()
            }
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        // Implement actual password change logic here
        // This would typically validate the current password against Firebase Auth
        // and then update to the new password
        
        try {
            // Simulate password verification and change
            // In a real app, this would check against Firebase Auth
            val user = auth.currentUser
            
            if (user != null) {
                // In a real implementation, use Firebase Auth's reauthenticate and updatePassword
                // For now, we'll simulate success
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun clearAppData(context: android.content.Context) {
        // Initialize if not already initialized
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences("gmao_machines_prefs", Context.MODE_PRIVATE)
        }
        
        // Clear shared preferences
        sharedPreferences.edit().clear().apply()
        
        // Clear cache if needed
        try {
            val cacheDir = context.cacheDir
            deleteDir(cacheDir)
        } catch (e: Exception) {
            // Handle exception
        }
    }

    private fun deleteDir(dir: java.io.File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(java.io.File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return dir?.delete() ?: true
    }
} 