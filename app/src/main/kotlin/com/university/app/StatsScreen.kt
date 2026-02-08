

package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.app.network.LeaderboardItem
import com.university.app.ui.theme.LeedsGreen
import com.university.app.ui.theme.PointsColor
import com.university.app.ui.theme.White
import com.university.app.ui.theme.spaceMonoFamily

val leaderboardItems = listOf(
    LeaderboardItem(1, "Student A", "1,250"),
    LeaderboardItem(2, "Student B", "980"),
    LeaderboardItem(3, "Student C", "750"),
    LeaderboardItem(4, "Student D", "620"),
    LeaderboardItem(5, "You", "0")
)

@Composable
fun StatsScreen(onBack: () -> Unit) {
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
