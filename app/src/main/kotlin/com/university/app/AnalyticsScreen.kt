
package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.app.ui.theme.LeedsGreen
import com.university.app.ui.theme.PointsColor
import com.university.app.ui.theme.White
import com.university.app.ui.theme.spaceMonoFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import com.university.app.network.LeaderboardItem

@Composable
fun AnalyticsScreen(onBack: () -> Unit, analyticsViewModel: AnalyticsViewModel = viewModel()) {
    val leaderboardItems by analyticsViewModel.leaderboardItems.collectAsState()
    analyticsViewModel.getLeaderboard()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Analytics Dashboard",
            color = LeedsGreen,
            fontSize = 48.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 50.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Chart("Attendance Over Time")
            Chart("Points Distribution")
        }
        Leaderboard(leaderboardItems)
    }
}
