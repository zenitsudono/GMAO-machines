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
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Sample machine data
    val machines = remember {
        listOf(
            Machine("A01", "Production Line Machine", MachineStatus.NEEDS_MAINTENANCE, today.plusDays(5)),
            Machine("B02", "Packaging Unit", MachineStatus.OPERATIONAL, today.plusDays(15)),
            Machine("C03", "Assembly Robot", MachineStatus.UNDER_MAINTENANCE, today.plusDays(2)),
            Machine("D04", "Quality Control Scanner", MachineStatus.OPERATIONAL, today.plusDays(20)),
            Machine("E05", "Conveyor System", MachineStatus.NEEDS_MAINTENANCE, today.plusDays(7))
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp) // Add padding for bottom navigation
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
                    Text(
                        "<",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
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
                    Text(
                        ">",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
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
                    .height(280.dp), // Fixed height for calendar grid
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { date ->
                    DayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        onClick = { if (date != null) selectedDate = date }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Machines List Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Machines Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                machines.forEach { machine ->
                    MachineItem(machine = machine)
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MachineItem(machine: Machine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (machine.status) {
                    MachineStatus.NEEDS_MAINTENANCE -> Color(0xFFFFF3E0).copy(alpha = 0.5f)
                    MachineStatus.UNDER_MAINTENANCE -> Color(0xFFE3F2FD).copy(alpha = 0.5f)
                    MachineStatus.OPERATIONAL -> Color(0xFFE8F5E9).copy(alpha = 0.5f)
                }
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = when (machine.status) {
                    MachineStatus.NEEDS_MAINTENANCE -> Icons.Default.Warning
                    MachineStatus.UNDER_MAINTENANCE -> Icons.Default.Build
                    MachineStatus.OPERATIONAL -> Icons.Default.CheckCircle
                },
                contentDescription = "Machine Status",
                tint = when (machine.status) {
                    MachineStatus.NEEDS_MAINTENANCE -> Color(0xFFF57C00)
                    MachineStatus.UNDER_MAINTENANCE -> Color(0xFF1976D2)
                    MachineStatus.OPERATIONAL -> Color(0xFF43A047)
                }
            )
            Column {
                Text(
                    text = "${machine.id} - ${machine.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (machine.status) {
                        MachineStatus.NEEDS_MAINTENANCE -> "Needs Maintenance"
                        MachineStatus.UNDER_MAINTENANCE -> "Under Maintenance"
                        MachineStatus.OPERATIONAL -> "Operational"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "Next: ${machine.nextMaintenanceDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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