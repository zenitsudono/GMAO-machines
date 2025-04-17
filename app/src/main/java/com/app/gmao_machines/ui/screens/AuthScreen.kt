package com.app.gmao_machines.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
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
import com.app.gmao_machines.ui.components.RegisterContent
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

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.handleGoogleSignInResult(result)
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }

    Scaffold{ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Spacer(modifier = Modifier.height(24.dp))

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
                            }
                        )
                    } else {
                        RegisterContent(viewModel = viewModel)
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