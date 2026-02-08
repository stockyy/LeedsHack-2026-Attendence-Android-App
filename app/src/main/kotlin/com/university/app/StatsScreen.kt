package com.university.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.university.app.network.ApiClient
import com.university.app.network.StudentStats

@Composable
fun StatsScreen(studentId: Int, onBack: () -> Unit) {
    var stats by remember { mutableStateOf<StudentStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(studentId) {
        stats = ApiClient.getStudentStats(studentId)
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Performance", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (stats == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Failed to load statistics.")
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { PointsCard(stats!!.totalPoints) }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { AttendanceCard(stats!!.overallAttendance) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OverallQuizStatsCard(stats!!.overallAverage)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Module Breakdown", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(stats!!.moduleStats) { module ->
                    ModuleStatsItem(
                        module.moduleCode,
                        module.averageScore,
                        module.totalQuizzes,
                        module.attendancePercentage
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun PointsCard(points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            Text(text = "Rewards", style = MaterialTheme.typography.labelMedium)
            Text(text = "$points pts", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun AttendanceCard(percentage: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Text(text = "Attendance", style = MaterialTheme.typography.labelMedium)
            Text(text = "${"%.0f".format(percentage)}%", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun OverallQuizStatsCard(overallAverage: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Overall Quiz Average", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${"%.1f".format(overallAverage)}%",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ModuleStatsItem(moduleCode: String, averageScore: Double, totalQuizzes: Int, attendance: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = moduleCode, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${"%.0f".format(attendance)}% Attended",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (attendance < 75) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { (attendance / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$totalQuizzes Quizzes", style = MaterialTheme.typography.bodySmall)
                Text(text = "Avg: ${"%.1f".format(averageScore)}%", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
