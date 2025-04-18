package com.app.gmao_machines.ui.viewModel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gmao_machines.data.User
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
                    _uiState.value = AuthUiState.Success(
                        User(
                            email = email.value,
                            firstName = firstName.value,
                            lastName = lastName.value
                        )
                    )
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
                _uiState.value = AuthUiState.Success(user)
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
        Log.d("AuthViewModel", "Handling Google sign-in result, resultCode: ${result.resultCode}")
        try {
            if (result.data == null) {
                Log.e("AuthViewModel", "Google sign-in result data is null")
                _uiState.value = AuthUiState.Error("Sign-in failed: No data returned")
                return
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (!task.isSuccessful) {
                Log.e("AuthViewModel", "Google sign-in task was not successful")
                _uiState.value = AuthUiState.Error("Sign-in failed: Authentication task failed")
                return
            }

            val account = task.getResult(ApiException::class.java)
            if (account == null) {
                Log.e("AuthViewModel", "Google sign-in account is null")
                _uiState.value = AuthUiState.Error("Sign-in failed: No account returned")
                return
            }

            Log.d("AuthViewModel", "Got Google account, ID: ${account.id}, Email: ${account.email}")
            
            // Check token
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                Log.e("AuthViewModel", "Google sign-in returned null or blank ID token")
                _uiState.value = AuthUiState.Error("Sign-in failed: Missing authentication token")
                return
            }
            
            Log.d("AuthViewModel", "Got ID token of length ${idToken.length}, authenticating with Firebase")
            _uiState.value = AuthUiState.Loading
            
            viewModelScope.launch {
                try {
                    Log.d("AuthViewModel", "Calling repository signInWithGoogle")
                    val user = repository.signInWithGoogle(idToken)
                    Log.d("AuthViewModel", "Successfully signed in with Google, user: ${user.email}")
                    _uiState.value = AuthUiState.Success(user)
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error signing in with Google", e)
                    _uiState.value = AuthUiState.Error(e.message ?: "Google sign-in failed")
                }
            }
        } catch (e: ApiException) {
            Log.e("AuthViewModel", "Google sign-in API Exception: code=${e.statusCode}, message=${e.message}", e)
            _uiState.value = AuthUiState.Error("Google sign-in failed: code ${e.statusCode}")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Unexpected exception during Google sign-in", e)
            _uiState.value = AuthUiState.Error("Google sign-in failed: ${e.message}")
        }
    }

    private fun validateRegistrationFields(): Boolean {
        when {
            firstName.value.isBlank() -> {
                _errorMessage.value = "First name cannot be empty"
                return false
            }
            lastName.value.isBlank() -> {
                _errorMessage.value = "Last name cannot be empty"
                return false
            }
            email.value.isBlank() -> {
                _errorMessage.value = "Email cannot be empty"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> {
                _errorMessage.value = "Invalid email format"
                return false
            }
            password.value.isBlank() -> {
                _errorMessage.value = "Password cannot be empty"
                return false
            }
            !isPasswordValid(password.value) -> {
                _errorMessage.value = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
                return false
            }
            confirmPassword.value != password.value -> {
                _errorMessage.value = "Passwords do not match"
                return false
            }
            !termsAccepted.value -> {
                _errorMessage.value = "You must accept the Terms of Service and Privacy Policy"
                return false
            }
            else -> return true
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        // Minimum 8 characters, at least one uppercase, one lowercase, one number, one special character
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun clearError() {
        _errorMessage.value = null
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Initial
        }
    }
}