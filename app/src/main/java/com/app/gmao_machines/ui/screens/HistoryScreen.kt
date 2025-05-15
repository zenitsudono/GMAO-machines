package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.data.Intervention
import com.app.gmao_machines.data.InterventionStatus
import com.app.gmao_machines.ui.viewModel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onInterventionClick: (Intervention) -> Unit
) {
    val interventions by viewModel.interventions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Intervention History") },
            actions = {
                IconButton(onClick = { viewModel.refreshInterventions() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshInterventions() }) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(interventions) { intervention ->
                        InterventionCard(
                            intervention = intervention,
                            dateFormat = dateFormat,
                            onStatusChange = { newStatus ->
                                viewModel.updateInterventionStatus(intervention.id, newStatus)
                            },
                            onClick = { onInterventionClick(intervention) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionCard(
    intervention: Intervention,
    dateFormat: SimpleDateFormat,
    onStatusChange: (InterventionStatus) -> Unit,
    onClick: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = intervention.intervenantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    StatusChip(status = intervention.status)
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        InterventionStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " ")) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Intervention Date: ${dateFormat.format(intervention.dateIntervention)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Planned Date: ${dateFormat.format(intervention.datePrevue)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Completion Date: ${dateFormat.format(intervention.dateRealisation)}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (intervention.details.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Operations:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                intervention.details.forEach { detail ->
                    Text(
                        text = "• ${detail.operationName} (Note: ${detail.note})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: InterventionStatus) {
    val (backgroundColor, textColor) = when (status) {
        InterventionStatus.PENDING -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        InterventionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        InterventionStatus.COMPLETED -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        InterventionStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = textColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
} 