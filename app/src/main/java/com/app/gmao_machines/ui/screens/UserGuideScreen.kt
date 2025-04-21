package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("User Guide") },
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
            // Introduction
            Text(
                text = "Getting Started with GMAO Machines",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "This guide will help you navigate the app and make the most of its features.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Section 1: Navigation
            GuideSection(
                title = "1. Navigation",
                content = """
                    The app has three main tabs accessible from the bottom navigation bar:
                    
                    • Home: View your dashboard, machines overview, and recent activities
                    • History: Access maintenance history and past records
                    • Profile: Manage your account settings and preferences
                """.trimIndent()
            )
            
            // Section 2: Managing Machines
            GuideSection(
                title = "2. Managing Machines",
                content = """
                    To add a new machine:
                    
                    1. Tap the "+" button on the home screen
                    2. Fill in the required information (name, type, model, etc.)
                    3. Optionally, add a photo or additional specifications
                    4. Tap "Save" to add the machine to your inventory
                    
                    To view machine details, simply tap on any machine from the list.
                """.trimIndent()
            )
            
            // Section 3: Scheduling Maintenance
            GuideSection(
                title = "3. Scheduling Maintenance",
                content = """
                    Regular maintenance is essential for your machinery:
                    
                    1. Open a machine's details page
                    2. Tap "Schedule Maintenance"
                    3. Select a maintenance type (routine, inspection, repair)
                    4. Choose a date and time
                    5. Add any notes or requirements
                    6. Confirm your schedule
                    
                    You'll receive notifications when maintenance is due.
                """.trimIndent()
            )
            
            // Section 4: Reporting Issues
            GuideSection(
                title = "4. Reporting Issues",
                content = """
                    When a machine has a problem:
                    
                    1. Open the machine's details page
                    2. Tap "Report Issue"
                    3. Select an issue type and severity
                    4. Describe the problem in detail
                    5. Optionally, add photos of the issue
                    6. Submit the report
                    
                    Track the status of your reports in the History tab.
                """.trimIndent()
            )
            
            // Section 5: Generating Reports
            GuideSection(
                title = "5. Generating Reports",
                content = """
                    Create detailed reports for your maintenance activities:
                    
                    1. Go to the History tab
                    2. Tap "Reports" at the top
                    3. Select the report type (daily, weekly, monthly)
                    4. Choose machines to include and date range
                    5. Tap "Generate Report"
                    6. View, download or share the report
                    
                    Reports can be exported in PDF or CSV format.
                """.trimIndent()
            )
            
            // Section 6: Account Settings
            GuideSection(
                title = "6. Account Settings",
                content = """
                    Manage your account from the Profile tab:
                    
                    • Edit Profile: Update your personal information
                    • Notifications: Set your notification preferences
                    • Theme: Toggle between light and dark mode
                    • Privacy & Security: Manage password and data settings
                    • Help & Support: Get assistance when needed
                """.trimIndent()
            )
            
            // Tips and Best Practices
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tips & Best Practices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    BestPracticeItem("Keep your machine inventory up-to-date for accurate tracking")
                    BestPracticeItem("Schedule preventive maintenance to avoid unexpected breakdowns")
                    BestPracticeItem("Document all repairs and maintenance activities thoroughly")
                    BestPracticeItem("Regularly review maintenance history to identify patterns")
                    BestPracticeItem("Use tags and categories to organize your machines efficiently")
                }
            }
            
            // Need More Help Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need More Help?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Contact our support team at support@gmao-machines.com or visit the Help & Support section.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun BestPracticeItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
} 