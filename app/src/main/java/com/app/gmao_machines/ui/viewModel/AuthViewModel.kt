package com.app.gmao_machines.ui.viewModel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gmao_machines.models.AuthUiState
import com.app.gmao_machines.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository()

    // UI state management
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Form fields
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _termsAccepted = MutableStateFlow(false)
    val termsAccepted = _termsAccepted.asStateFlow()

    // Error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Password visibility
    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    private val _confirmPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = _confirmPasswordVisible.asStateFlow()

    // GoogleSignInClient setup
    private var googleSignInClient: GoogleSignInClient

    init {
        try {
            // Create a complete GSO with full OAuth configuration
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(com.app.gmao_machines.R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build()
            
            // Force account selection each time
            googleSignInClient = GoogleSignIn.getClient(application, gso)
            googleSignInClient.signOut() // Clear any previous sign-in state
            
            Log.d("AuthViewModel", "Successfully initialized GoogleSignInClient")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error initializing GoogleSignInClient", e)
            throw e
        }
    }

    // Field updaters
    fun updateFirstName(value: String) {
        _firstName.value = value
    }

    fun updateLastName(value: String) {
        _lastName.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun updateTermsAccepted(value: Boolean) {
        _termsAccepted.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = !_confirmPasswordVisible.value
    }
    
    /**
     * Clears any error messages and resets the UI state if it's currently in an error state
     */
    fun clearError() {
        _errorMessage.value = null
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Initial
        }
    }

    // Login and Registration methods
    fun register() {
        _uiState.value = AuthUiState.Loading

        // Field validation
        if (!validateRegistrationFields()) {
            _uiState.value = AuthUiState.Error(_errorMessage.value ?: "Validation failed")
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.registerUser(
                    firstName.value,
                    lastName.value,
                    email.value,
                    password.value
                )

                if (result) {
                    // Show registration success with verification required
                    _uiState.value = AuthUiState.RegistrationSuccess(email.value)
                } else {
                    _uiState.value = AuthUiState.Error("Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun signIn() {
        _uiState.value = AuthUiState.Loading

        // Field validation
        if (email.value.isBlank() || password.value.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty"
            _uiState.value = AuthUiState.Error(_errorMessage.value!!)
            return
        }

        viewModelScope.launch {
            try {
                val user = repository.signInUser(email.value, password.value)
                
                // Check if email is verified
                val isVerified = repository.isEmailVerified()
                
                if (isVerified) {
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    // Email not verified, prompt user to verify
                    _uiState.value = AuthUiState.VerificationRequired(email.value)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Google sign-in related methods
    fun getGoogleSignInIntent(): Intent {
        Log.d("AuthViewModel", "Getting Google sign-in intent")
        // Sign out to always show the account picker
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        _uiState.value = AuthUiState.Loading
        
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            
            // Got Google account, now authenticate with Firebase
            viewModelScope.launch {
                try {
                    val user = repository.signInWithGoogle(account.idToken!!)
                    _uiState.value = AuthUiState.Success(user)
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Firebase Auth with Google failed", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Google authentication failed")
                }
            }
        } catch (e: ApiException) {
            Log.e("AuthViewModel", "Google sign in failed", e)
            _uiState.value = AuthUiState.Error("Google sign-in failed: ${e.statusCode}")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Unknown error in Google sign in", e)
            _uiState.value = AuthUiState.Error("Google sign-in failed: ${e.message}")
        }
    }
    
    fun forgotPassword(email1: String) {
        if (email.value.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email address")
            return
        }
        
        _uiState.value = AuthUiState.Loading
        
        viewModelScope.launch {
            try {
                val result = repository.sendPasswordResetEmail(email.value)
                
                if (result) {
                    _uiState.value = AuthUiState.PasswordResetSent(email.value)
                } else {
                    _uiState.value = AuthUiState.Error("Failed to send password reset email")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to send password reset email")
            }
        }
    }
    
    fun resendVerificationEmail() {
        _uiState.value = AuthUiState.Loading
        
        viewModelScope.launch {
            try {
                val result = repository.resendVerificationEmail()
                
                if (result) {
                    _uiState.value = AuthUiState.VerificationEmailSent(email.value)
                } else {
                    _uiState.value = AuthUiState.Error("Failed to send verification email")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error sending verification email: ${e.message}", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to send verification email")
            }
        }
    }
    
    private fun validateRegistrationFields(): Boolean {
        // Check first name
        if (firstName.value.isBlank()) {
            _errorMessage.value = "First name cannot be empty"
            return false
        }
        
        // Check last name
        if (lastName.value.isBlank()) {
            _errorMessage.value = "Last name cannot be empty"
            return false
        }
        
        // Check email
        if (email.value.isBlank() || !email.value.contains("@") || !email.value.contains(".")) {
            _errorMessage.value = "Please enter a valid email address"
            return false
        }
        
        // Check password
        if (password.value.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return false
        }
        
        // Check password confirmation
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Passwords do not match"
            return false
        }
        
        // Check terms acceptance
        if (!termsAccepted.value) {
            _errorMessage.value = "You must accept the terms and conditions"
            return false
        }
        
        return true
    }
}