package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.models.CountryCode
import com.app.gmao_machines.ui.components.CountryCodeDropdown
import com.app.gmao_machines.ui.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Collect user info
    val userInfo by profileViewModel.userInfo.collectAsState()
    
    // Form state variables
    var displayName by remember { mutableStateOf(userInfo.displayName) }
    var phoneNumber by remember { mutableStateOf(userInfo.phoneNumber) }
    var selectedCountry by remember { mutableStateOf(userInfo.countryCode) }
    var jobTitle by remember { mutableStateOf(userInfo.jobTitle) }
    var department by remember { mutableStateOf(userInfo.department) }
    var cin by remember { mutableStateOf(userInfo.cin) }
    
    // UI state variables
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Scaffold with TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Name Field
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email Field (read-only)
            OutlinedTextField(
                value = userInfo.email,
                onValueChange = { },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                enabled = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone Number Field with Country Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country code dropdown
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    CountryCodeDropdown(
                        selectedCountry = selectedCountry,
                        onCountrySelected = { selectedCountry = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Phone number field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Job Title Field
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Department Field
            OutlinedTextField(
                value = department,
                onValueChange = { department = it },
                label = { Text("Department") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // CIN Field (National Identity Card)
            OutlinedTextField(
                value = cin,
                onValueChange = { cin = it },
                label = { Text("CIN (National ID)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            // Call ViewModel method to update profile
                            val success = profileViewModel.updateProfile(
                                displayName = displayName,
                                phoneNumber = phoneNumber,
                                countryCode = selectedCountry,
                                jobTitle = jobTitle,
                                department = department,
                                cin = cin
                            )
                            
                            if (success) {
                                showSuccessMessage = true
                                // Wait briefly then navigate back
                                kotlinx.coroutines.delay(1500)
                                onBackClick()
                            } else {
                                errorMessage = "Failed to update profile"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "An error occurred"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && displayName.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cancel Button
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
        
        // Success message
        if (showSuccessMessage) {
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Profile updated successfully")
            }
        }
        
        // Error message
        if (errorMessage != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage ?: "")
            }
        }
    }
} 