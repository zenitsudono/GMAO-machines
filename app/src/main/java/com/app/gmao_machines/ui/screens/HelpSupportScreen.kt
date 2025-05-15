package com.app.gmao_machines.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    
    var showContactDialog by remember { mutableStateOf(false) }
    var showUserGuideScreen by remember { mutableStateOf(false) }
    var showReportIssueScreen by remember { mutableStateOf(false) }
    
    // Show nested screens if needed
    if (showUserGuideScreen) {
        UserGuideScreen(onBackClick = { showUserGuideScreen = false })
        return
    }
    
    if (showReportIssueScreen) {
        ReportIssueScreen(onBackClick = { showReportIssueScreen = false })
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Help & Support") },
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
            // Support Options Section
            SectionTitle(title = "Support Options")
            
            // Contact Support
            SettingsItem(
                icon = Icons.Default.Email,
                title = "Contact Support",
                subtitle = "Get help from our team",
                onClick = { showContactDialog = true }
            )
            
            // Report an Issue
            SettingsItem(
                icon = Icons.Default.BugReport,
                title = "Report an Issue",
                subtitle = "Tell us about any problems you've encountered",
                onClick = { showReportIssueScreen = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Help Center
            SectionTitle(title = "Help Center")
            
            // User Guide
            SettingsItem(
                icon = Icons.Default.MenuBook,
                title = "User Guide",
                subtitle = "Learn how to use the app",
                onClick = { showUserGuideScreen = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // FAQ Section
            SectionTitle(title = "Frequently Asked Questions")
            
            // FAQs
            FaqItem(
                question = "How do I add a new machine?",
                answer = "To add a new machine, go to the home screen and tap the '+' button at the bottom. Fill in the required information and tap 'Save'."
            )
            
            FaqItem(
                question = "How do I schedule maintenance?",
                answer = "Navigate to a machine's details page, then tap on 'Schedule Maintenance'. Select a date and maintenance type, then confirm your schedule."
            )
            
            FaqItem(
                question = "Can I export maintenance reports?",
                answer = "Yes, you can export reports in PDF or CSV format. Go to the Reports tab, select the report you want to export, then tap the 'Export' icon."
            )
            
            FaqItem(
                question = "How do I reset my password?",
                answer = "On the login screen, tap 'Forgot Password', enter your email address, and follow the instructions sent to your email."
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // About Section
            SectionTitle(title = "About")
            
            // App Version
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "App Version",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Contact Support Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact Support") },
            text = { 
                Column {
                    Text("Send us an email at:")
                    Text(
                        "support@gmao-machines.com",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Text("Or call us during business hours:")
                    Text(
                        "+1 (555) 123-4567",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Close")
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
internal fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (subtitle != null) {
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

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }
    val expandTransition = remember { MutableTransitionState(false) }
    
    expandTransition.targetState = expanded
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            // Question (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Answer (only visible when expanded)
            AnimatedVisibility(
                visibleState = expandTransition,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (expanded) {
                Divider(
                    modifier = Modifier.padding(start = 40.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
} 