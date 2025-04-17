package com.app.gmao_machines.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.models.AuthUiState
import com.app.gmao_machines.ui.components.RegisterContent
import com.app.gmao_machines.ui.components.SignInContent
import com.app.gmao_machines.ui.viewModel.AuthViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isSignIn by remember { mutableStateOf(true) }

    val uiState = viewModel.uiState.collectAsState().value
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
        when (uiState) {
            is AuthUiState.Success -> {
                onAuthSuccess()
            }
            else -> {} // Handle other states as needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = if (isSignIn) "Welcome back!" else "Register",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        // Tab buttons for switching between Sign In and Register
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            tabs = {
                OutlinedButton(
                    onClick = { isSignIn = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSignIn) Color.LightGray else Color.White
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                ) {
                    Text("Sign In")
                }

                OutlinedButton(
                    onClick = { isSignIn = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isSignIn) Color.LightGray else Color.White
                    ),
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                ) {
                    Text("Register")
                }
            },
            selectedTabIndex = if (isSignIn) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = {},
            divider = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSignIn) {
            SignInContent(
                viewModel = viewModel,
                onGoogleSignIn = {
                    googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
                }
            )
        } else {
            RegisterContent(viewModel = viewModel)
        }

        // Loading indicator and error handling
        when (uiState) {
            is AuthUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            is AuthUiState.Error -> {
                val errorMessage = (uiState as AuthUiState.Error).message
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {} // Handle other states as needed
        }
    }
}