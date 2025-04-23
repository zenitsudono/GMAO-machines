package com.app.gmao_machines.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.app.gmao_machines.R
import com.app.gmao_machines.models.CountryCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserInfo(
    val displayName: String = "User",
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: CountryCode = CountryCode.DEFAULT,
    val jobTitle: String = "",
    val department: String = "",
    val cin: String = "",
    val machineCount: Int = 0,
    val maintenanceCount: Int = 0,
    val reportCount: Int = 0
) {
    // Format the phone number with country code for display
    val formattedPhoneNumber: String
        get() = if (phoneNumber.isNotBlank()) {
            "+${countryCode.dialCode} $phoneNumber"
        } else {
            ""
        }
}

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
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
                
            viewModelScope.launch {
                try {
                    // Try to load additional user info from Firestore
                    val userId = currentUser.uid
                    val userDoc = firestore.collection("users").document(userId).get().await()
                    
                    if (userDoc.exists()) {
                        // Get phone and extract country code if available
                        val rawPhone = userDoc.getString("phoneNumber") ?: ""
                        val countryDialCode = userDoc.getString("countryDialCode") ?: CountryCode.DEFAULT.dialCode
                        val countryCode = CountryCode.findByDialCode(countryDialCode)
                        
                        // For migration: If we have a full phone with +, extract country code
                        val (extractedCountryCode, localNumber) = if (rawPhone.startsWith("+")) {
                            CountryCode.extractCountryCodeFromPhone(rawPhone)
                        } else {
                            Pair(countryCode, rawPhone)
                        }
                        
                        _userInfo.value = _userInfo.value.copy(
                            displayName = displayName.toString(),
                            email = currentUser.email ?: "",
                            phoneNumber = localNumber,
                            countryCode = extractedCountryCode,
                            jobTitle = userDoc.getString("jobTitle") ?: "",
                            department = userDoc.getString("department") ?: "",
                            cin = userDoc.getString("cin") ?: ""
                        )
                    } else {
                        // If no additional data exists, just update basic info
                        _userInfo.value = _userInfo.value.copy(
                            displayName = displayName.toString(),
                            email = currentUser.email ?: ""
                        )
                    }
                } catch (e: Exception) {
                    // If Firestore fetch fails, still update the basic info
                    Log.e("ProfileViewModel", "Error loading user data from Firestore", e)
                    _userInfo.value = _userInfo.value.copy(
                        displayName = displayName.toString(),
                        email = currentUser.email ?: ""
                    )
                }
            }
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

    suspend fun updateProfile(
        displayName: String,
        phoneNumber: String,
        countryCode: CountryCode = _userInfo.value.countryCode,
        jobTitle: String,
        department: String,
        cin: String
    ): Boolean {
        try {
            val user = auth.currentUser ?: return false
            
            // Update display name in Firebase Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
                
            user.updateProfile(profileUpdates).await()
            
            // Store additional fields in Firestore
            val userData = hashMapOf(
                "displayName" to displayName,
                "email" to (user.email ?: ""),
                "phoneNumber" to phoneNumber,
                "countryDialCode" to countryCode.dialCode, // Store country dial code separately
                "jobTitle" to jobTitle,
                "department" to department,
                "cin" to cin,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection("users")
                .document(user.uid)
                .set(userData)
                .await()
                
            // Update local state
            _userInfo.value = _userInfo.value.copy(
                displayName = displayName,
                phoneNumber = phoneNumber,
                countryCode = countryCode,
                jobTitle = jobTitle,
                department = department,
                cin = cin
            )
            
            return true
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error updating profile", e)
            return false
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