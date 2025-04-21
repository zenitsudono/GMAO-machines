package com.app.gmao_machines.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.gmao_machines.data.IssueReport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlinx.coroutines.launch

private const val TAG = "ReportIssueScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore
    
    // Define the collection reference and log its ID
    val issueReportsCollection = firestore.collection("issueReports")
    val collectionId = issueReportsCollection.id
    
    // Log the collection ID for verification
    LaunchedEffect(Unit) {
        Log.d(TAG, "Using Firestore collection: $collectionId")
    }
    
    // UI state
    var isSubmitting by remember { mutableStateOf(false) }
    
    // Form state
    var issueType by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Medium") }
    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Initialize email with current user's email if available
    LaunchedEffect(Unit) {
        auth.currentUser?.email?.let {
            email = it
        }
    }
    
    // Issue Types
    val issueTypes = listOf(
        "App Functionality", 
        "UI/Display Issues", 
        "Machine Data Error", 
        "Synchronization Problem", 
        "Performance Issue",
        "Login/Authentication Problem",
        "Other"
    )
    
    // Severity Levels
    val severityLevels = listOf("Low", "Medium", "High", "Critical")
    
    // Validation
    val isFormValid = issueType.isNotEmpty() && description.length >= 10
    
    // Function to submit issue report to Firebase
    val submitIssueReport = {
        scope.launch {
            try {
                isSubmitting = true
                
                val currentUser = auth.currentUser
                val userId = currentUser?.uid ?: "anonymous"
                val userEmail = currentUser?.email ?: "anonymous"
                
                val issueReport = IssueReport(
                    userId = userId,
                    userEmail = userEmail,
                    issueType = issueType,
                    severity = severity,
                    description = description,
                    contactEmail = email,
                    attachmentCount = attachments,
                    status = "New",
                    createdAt = Date()
                )
                
                // Add to Firestore and log document ID
                val documentRef = issueReportsCollection.add(issueReport).await()
                Log.d(TAG, "Issue report submitted with ID: ${documentRef.id}")
                
                isSubmitting = false
                showSuccessDialog = true
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting issue report", e)
                isSubmitting = false
                Toast.makeText(
                    context,
                    "Error submitting report: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Report an Issue") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            // Issue Form
            Text(
                text = "Please provide details about the issue you're experiencing",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Issue Type Dropdown
            Text(
                text = "Issue Type*",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = issueType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    placeholder = { Text("Select issue type") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    issueTypes.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                issueType = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Severity Selection
            Text(
                text = "Severity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Severity Radio Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                severityLevels.forEach { level ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = severity == level,
                            onClick = { severity = level },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = when (level) {
                                    "Low" -> MaterialTheme.colorScheme.tertiary
                                    "Medium" -> MaterialTheme.colorScheme.primary
                                    "High" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                        )
                        Text(
                            text = level,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Description Field
            Text(
                text = "Description*",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Please describe the issue in detail...") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                minLines = 5,
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Email for Response
            Text(
                text = "Your Email (for updates)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email address") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            // Attachment Button
            OutlinedButton(
                onClick = { 
                    if (attachments < 3) {
                        attachments++
                        Toast.makeText(context, "Attachment added (simulated)", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Maximum 3 attachments allowed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Screenshots or Photos ($attachments/3)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Our support team typically responds within 24-48 hours on business days.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Bottom Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = { 
                        if (isFormValid) {
                            submitIssueReport()
                        } else {
                            Toast.makeText(context, "Please fill out all required fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isFormValid && !isSubmitting,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit")
                    }
                }
            }
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            title = { Text("Issue Reported") },
            text = { 
                Text(
                    "Thank you for your report. Our support team has been notified and will investigate the issue." +
                    if (email.isNotEmpty()) " We'll provide updates to $email." else ""
                ) 
            },
            confirmButton = { 
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBackClick()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
} 