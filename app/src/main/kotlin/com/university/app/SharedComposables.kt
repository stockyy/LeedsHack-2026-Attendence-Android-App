
package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
fun ModuleAttendanceChart() {
    val moduleAttendance = mapOf(
        "COMP2850" to 85,
        "COMP2860" to 70,
        "COMP2870" to 95
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1A00502F))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Attendance by Module",
            color = LeedsGreen,
            fontSize = 24.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            moduleAttendance.forEach { (module, percentage) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = module,
                        color = White,
                        fontFamily = spaceMonoFamily,
                        modifier = Modifier.width(100.dp)
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = percentage / 100f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(LeedsGreen)
                        )
                    }
                    Text(
                        text = "$percentage%",
                        color = White,
                        fontFamily = spaceMonoFamily,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .width(50.dp)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceOverTimeChart() {
    val attendanceOverTime = mapOf(
        "Sep" to 80,
        "Oct" to 85,
        "Nov" to 75,
        "Dec" to 70,
        "Jan" to 88,
        "Feb" to 90,
        "Mar" to 92
    )
    val maxValue = attendanceOverTime.values.maxOrNull() ?: 100

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1A00502F))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Attendance Over Time",
            color = LeedsGreen,
            fontSize = 24.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            attendanceOverTime.forEach { (month, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$value",
                        color = White,
                        fontSize = 10.sp,
                        fontFamily = spaceMonoFamily,
                    )
                    Box(
                        modifier = Modifier
                            .width(30.dp) // Bar width
                            .height((value.toFloat() / maxValue.toFloat() * 150).dp) // Bar height calculation
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(LeedsGreen)
                    )
                    Text(
                        text = month,
                        color = White,
                        fontSize = 12.sp,
                        fontFamily = spaceMonoFamily,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
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
