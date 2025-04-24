package com.app.gmao_machines.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.ui.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State variables
    var passwordChangeDialogVisible by remember { mutableStateOf(false) }
    var clearDataDialogVisible by remember { mutableStateOf(false) }
    
    // Initialize ProfileViewModel
    LaunchedEffect(Unit) {
        profileViewModel.initialize(context)
    }
    
    // Password change states
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var passwordErrorText by remember { mutableStateOf("") }
    
    // Function to handle clear app data
    val handleClearData = {
        scope.launch {
            profileViewModel.clearAppData(context)
            Toast.makeText(context, "App data cleared successfully", Toast.LENGTH_SHORT).show()
            clearDataDialogVisible = false
        }
    }
    
    // Main content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Privacy & Security") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
        
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Account Security Section
            SectionTitle(title = "Account Security")
            
            // Change Password
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Change Password",
                subtitle = "Change your account password",
                onClick = { passwordChangeDialogVisible = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Privacy Section
            SectionTitle(title = "Privacy")
            
            // Clear Data
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear App Data",
                subtitle = "Delete all app data from this device",
                onClick = { clearDataDialogVisible = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Legal Section
            SectionTitle(title = "Legal")
            
            // Privacy Policy
            SettingsItem(
                icon = Icons.Default.Description,
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gmao-machines.com/privacy-policy"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            // Terms of Service
            SettingsItem(
                icon = Icons.Default.Article,
                title = "Terms of Service",
                subtitle = "View our terms of service",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gmao-machines.com/terms-of-service"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
    
    // Password Change Dialog
    if (passwordChangeDialogVisible) {
        AlertDialog(
            onDismissRequest = { passwordChangeDialogVisible = false },
            title = {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = { 
                Column {
                    if (isPasswordError) {
                        Text(
                            text = passwordErrorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Current Password
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it; isPasswordError = false },
                        label = { Text("Current Password") },
                        singleLine = true,
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    // New Password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; isPasswordError = false },
                        label = { Text("New Password") },
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; isPasswordError = false },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            when {
                                currentPassword.isEmpty() -> {
                                    isPasswordError = true
                                    passwordErrorText = "Current password is required"
                                }
                                newPassword.isEmpty() -> {
                                    isPasswordError = true
                                    passwordErrorText = "New password is required"
                                }
                                newPassword != confirmPassword -> {
                                    isPasswordError = true
                                    passwordErrorText = "Passwords do not match"
                                }
                                newPassword.length < 6 -> {
                                    isPasswordError = true
                                    passwordErrorText = "Password must be at least 6 characters"
                                }
                                else -> {
                                    try {
                                        profileViewModel.changePassword(currentPassword, newPassword)
                                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                        passwordChangeDialogVisible = false
                                        // Clear passwords
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    } catch (e: Exception) {
                                        isPasswordError = true
                                        passwordErrorText = e.message ?: "Failed to change password"
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Change Password")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        passwordChangeDialogVisible = false
                        // Clear passwords
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        isPasswordError = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Clear Data Confirmation Dialog
    if (clearDataDialogVisible) {
        AlertDialog(
            onDismissRequest = { clearDataDialogVisible = false },
            title = { Text("Clear App Data") },
            text = { 
                Text("This will clear all locally stored data including preferences and cached files. This action cannot be undone. Do you want to continue?")
            },
            confirmButton = {
                Button(
                    onClick = { handleClearData() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { clearDataDialogVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 