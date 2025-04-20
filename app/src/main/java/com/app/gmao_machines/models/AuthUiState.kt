package com.app.gmao_machines.models

import com.app.gmao_machines.data.User

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class PasswordResetSent(val email: String) : AuthUiState()
    data class VerificationEmailSent(val email: String) : AuthUiState()
    data class RegistrationSuccess(val email: String) : AuthUiState()
    data class VerificationRequired(val email: String) : AuthUiState()
}