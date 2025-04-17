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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("819982504390-d5pvdsgst01medghuarbi02jcotlr6fd.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(application, gso)
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
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            // Got Google account, now authenticate with your backend
            account?.idToken?.let { idToken ->
                viewModelScope.launch {
                    try {
                        _uiState.value = AuthUiState.Loading
                        val user = repository.signInWithGoogle(idToken)
                        _uiState.value = AuthUiState.Success(user)
                    } catch (e: Exception) {
                        _uiState.value = AuthUiState.Error(e.message ?: "Google sign-in failed")
                    }
                }
            } ?: run {
                _uiState.value = AuthUiState.Error("Google sign-in failed: No ID token")
            }
        } catch (e: ApiException) {
            _uiState.value = AuthUiState.Error("Google sign-in failed: ${e.statusCode}")
            Log.e("AuthViewModel", "Google sign-in failed", e)
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