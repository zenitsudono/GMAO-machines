package com.app.gmao_machines.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.models.AuthUiState
import com.app.gmao_machines.ui.components.EmailVerificationDialog
import com.app.gmao_machines.ui.components.RegisterContent
import com.app.gmao_machines.ui.components.RegistrationSuccessDialog
import com.app.gmao_machines.ui.components.SignInContent
import com.app.gmao_machines.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isSignIn by remember { mutableStateOf(true) }

    // Using collectAsStateWithLifecycle for better lifecycle awareness
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // State for dialogs
    var showEmailVerificationDialog by remember { mutableStateOf(false) }
    var showRegistrationSuccessDialog by remember { mutableStateOf(false) }
    var verificationEmail by remember { mutableStateOf("") }
    
    // Google Sign-In launcher with better error handling
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("AuthScreen", "Google sign-in result received: ${result.resultCode}")
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                // Check if we have data in the result
                if (result.data != null) {
                    Log.d("AuthScreen", "Google sign-in successful, handling result")
                    Toast.makeText(context, "Processing sign-in...", Toast.LENGTH_SHORT).show()
            viewModel.handleGoogleSignInResult(result)
                } else {
                    Log.e("AuthScreen", "Google sign-in resulted in null data")
                    Toast.makeText(context, "Sign-in error: No data returned", Toast.LENGTH_LONG).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.w("AuthScreen", "Google sign-in cancelled by user")
                Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.e("AuthScreen", "Google sign-in failed with unknown result code: ${result.resultCode}")
                Toast.makeText(context, "Sign-in failed with code: ${result.resultCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        Log.d("AuthScreen", "Auth UI state changed: $uiState")
        when (uiState) {
            is AuthUiState.Success -> {
                Log.d("AuthScreen", "Authentication successful, navigating to main screen")
                Toast.makeText(context, "Authentication successful", Toast.LENGTH_SHORT).show()
            onAuthSuccess()
            }
            is AuthUiState.Error -> {
                Log.e("AuthScreen", "Authentication error: ${(uiState as AuthUiState.Error).message}")
                Toast.makeText(
                    context,
                    "Authentication error: ${(uiState as AuthUiState.Error).message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            is AuthUiState.PasswordResetSent -> {
                val email = (uiState as AuthUiState.PasswordResetSent).email
                Log.d("AuthScreen", "Password reset email sent to $email")
                Toast.makeText(
                    context,
                    "Password reset instructions sent to $email",
                    Toast.LENGTH_LONG
                ).show()
                // Reset UI state after showing toast
                viewModel.clearError()
            }
            is AuthUiState.VerificationEmailSent -> {
                val email = (uiState as AuthUiState.VerificationEmailSent).email
                Log.d("AuthScreen", "Verification email resent to $email")
                Toast.makeText(
                    context,
                    "Verification email resent to $email",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearError()
            }
            is AuthUiState.RegistrationSuccess -> {
                val email = (uiState as AuthUiState.RegistrationSuccess).email
                Log.d("AuthScreen", "Registration successful, email verification required for $email")
                verificationEmail = email
                showRegistrationSuccessDialog = true
                viewModel.clearError()
            }
            is AuthUiState.VerificationRequired -> {
                val email = (uiState as AuthUiState.VerificationRequired).email
                Log.d("AuthScreen", "Email verification required for $email")
                verificationEmail = email
                showEmailVerificationDialog = true
            }
            is AuthUiState.Loading -> {
                Log.d("AuthScreen", "Authentication loading...")
            }
            else -> {
                Log.d("AuthScreen", "Initial authentication state")
            }
        }
    }

    // Show the appropriate dialogs based on state
    if (showEmailVerificationDialog) {
        EmailVerificationDialog(
            email = verificationEmail,
            onDismiss = { showEmailVerificationDialog = false },
            onResendEmail = { 
                viewModel.resendVerificationEmail()
                showEmailVerificationDialog = false
            },
            onSignIn = {
                showEmailVerificationDialog = false
                viewModel.signIn()
            }
        )
    }
    
    if (showRegistrationSuccessDialog) {
        RegistrationSuccessDialog(
            email = verificationEmail,
            onDismiss = { 
                showRegistrationSuccessDialog = false
                isSignIn = true // Switch to sign in screen
            },
            onGoToSignIn = {
                showRegistrationSuccessDialog = false
                isSignIn = true // Switch to sign in screen
            }
        )
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp) // Add padding at the bottom for better UX when scrolling
            ) {
                item {
                    // Welcome text
                    Text(
                        text = if (isSignIn) "Welcome back!" else "Create an account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 24.dp)
                            .semantics { contentDescription = "Authentication screen title" }
                    )
                }

                item {
                    // Tab buttons for switching between Sign In and Register
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(4.dp)
                        ) {
                            val tabModifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()

                            // Sign In tab
                            TabButton(
                                text = "Sign In",
                                isSelected = isSignIn,
                                onClick = { isSignIn = true },
                                modifier = tabModifier
                            )

                            // Register tab
                            TabButton(
                                text = "Register",
                                isSelected = !isSignIn,
                                onClick = { isSignIn = false },
                                modifier = tabModifier
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    // Content with animation
                    AnimatedContent(
                        targetState = isSignIn,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "Auth content animation"
                    ) { isSignInState ->
                        if (isSignInState) {
                            SignInContent(
                                viewModel = viewModel,
                                onGoogleSignIn = {
                                    googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
                                },
                                onRegisterClick = {
                                    isSignIn = false
                                },
                                onForgotPassword = { email ->
                                    viewModel.forgotPassword(email)
                                }
                            )
                        } else {
                            RegisterContent(
                                viewModel = viewModel,
                                onSignInClick = {
                                    isSignIn = true
                                }
                            )
                        }
                    }
                }
            }

            // Loading and error states
            when (uiState) {
                is AuthUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                is AuthUiState.Error -> {
                    val errorMessage = (uiState as AuthUiState.Error).message
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Snackbar(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Text(text = errorMessage)
                        }
                    }
                }
                else -> {} // Other states
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}