
package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun Chart(title: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1A00502F))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = LeedsGreen,
            fontSize = 24.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        // Placeholder for chart
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(250.dp)
                .background(Color.DarkGray)
        )
    }
}

@Composable
fun Leaderboard(leaderboardItems: List<LeaderboardItem>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1A00502F))
            .padding(30.dp)
    ) {
        Text(
            text = "Leaderboard",
            color = LeedsGreen,
            fontSize = 24.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )
        LazyColumn {
            items(leaderboardItems) { item ->
                LeaderboardItemView(item)
            }
        }
    }
}

@Composable
fun LeaderboardItemView(item: LeaderboardItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.rank.toString(),
            color = PointsColor,
            fontSize = 32.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = item.name,
            color = White,
            fontSize = 18.sp,
            fontFamily = spaceMonoFamily,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.score,
            color = PointsColor,
            fontSize = 20.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold
        )
    }
}
