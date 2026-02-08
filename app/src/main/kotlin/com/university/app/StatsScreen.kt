package com.university.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            OverallStatsCard(stats!!.overallAverage)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Module Breakdown", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(stats!!.moduleStats) { module ->
                    ModuleStatsItem(module.moduleCode, module.averageScore, module.totalQuizzes)
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
fun OverallStatsCard(overallAverage: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Overall Average Score", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${"%.1f".format(overallAverage)}%",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ModuleStatsItem(moduleCode: String, averageScore: Double, totalQuizzes: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = moduleCode, style = MaterialTheme.typography.titleMedium)
                Text(text = "$totalQuizzes quizzes completed", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "${"%.1f".format(averageScore)}%",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
