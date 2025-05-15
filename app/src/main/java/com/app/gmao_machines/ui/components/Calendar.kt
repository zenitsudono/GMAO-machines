package com.app.gmao_machines.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import com.app.gmao_machines.data.Intervention
import java.time.ZoneId

data class Machine(
    val id: String,
    val name: String,
    val status: MachineStatus,
    val nextMaintenanceDate: LocalDate
)

enum class MachineStatus {
    OPERATIONAL,
    NEEDS_MAINTENANCE,
    UNDER_MAINTENANCE
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    interventions: List<Intervention>,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Map interventions by date
    val interventionsByDate = remember(interventions) {
        interventions.groupBy {
            it.dateIntervention.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Notification Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFEBEE))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFE53935)
            )
            Text(
                text = "Maintenance check needed for Machine A01 on ${today.plusDays(5).format(DateTimeFormatter.ofPattern("MMM dd"))}",
                color = Color(0xFFB71C1C),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Month and Year header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Text("<", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Text(">", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayOfWeek in 1..7) {
                    val day = java.time.DayOfWeek.of(dayOfWeek)
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Calendar grid
            val days = getDaysInMonth(currentMonth)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { date ->
                    val hasIntervention = date != null && interventionsByDate.containsKey(date)
                    DayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        hasIntervention = hasIntervention,
                        onClick = { if (date != null) selectedDate = date }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Interventions List for Selected Day
        val selectedInterventions = interventionsByDate[selectedDate] ?: emptyList()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Interventions on ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (selectedInterventions.isEmpty()) {
                Text(
                    text = "No interventions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                selectedInterventions.forEach { intervention ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = intervention.intervenantName.ifBlank { "Intervention #${intervention.id}" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = intervention.status.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = when (intervention.status) {
                                    com.app.gmao_machines.data.InterventionStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                                    com.app.gmao_machines.data.InterventionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                                    com.app.gmao_machines.data.InterventionStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
                                    com.app.gmao_machines.data.InterventionStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                }
                            )
                            if (intervention.description != null) {
                                Text(
                                    text = intervention.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    hasIntervention: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isToday -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .then(if (date != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        isToday -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = when {
                        isSelected || isToday -> FontWeight.Bold
                        else -> FontWeight.Normal
                    }
                )
                if (hasIntervention) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate?> {
    val firstOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value
    val lastOfMonth = yearMonth.atEndOfMonth()
    
    val days = mutableListOf<LocalDate?>()
    
    // Add empty spaces for days before the first of the month
    repeat(firstDayOfWeek - 1) {
        days.add(null)
    }
    
    // Add all days of the month
    for (dayOfMonth in 1..lastOfMonth.dayOfMonth) {
        days.add(yearMonth.atDay(dayOfMonth))
    }
    
    // Add empty spaces to complete the grid if necessary
    while (days.size % 7 != 0) {
        days.add(null)
    }
    
    return days
} 