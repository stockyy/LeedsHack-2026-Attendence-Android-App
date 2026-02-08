package com.university.app

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.app.network.AuthResponse
import kotlinx.coroutines.delay

// --- Theme Colors from HTML ---
val LeedsGreen = Color(0xFF00502F)
val LeedsRed = Color(0xFFAF1B00)
val PointsColor = Color(0xFFFFD700)
val Black = Color(0xFF0A0A0A)
val MidGray = Color(0xFFE0E0E0)

@Composable
fun DashboardScreen(
    user: AuthResponse,
    onLogout: () -> Unit,
    points: Int,
    multiplier: Int,
    checkInStatus: String,
    scannedTag: String?,
    onPointsAdded: (Int) -> Unit,
    onResetStats: () -> Unit,
    onScanSimulated: (String) -> Unit
) {
    var activePage by remember { mutableStateOf("home") }

    val tierData = listOf(
        Tier("Tier 1", 100, "- Printing Credits \n- Freeze Streak"),
        Tier("Tier 2", 200, "- Double or Nothing"),
        Tier("Tier 3", 500, "- GFAL Points"),
        Tier("Tier 4", 600, "- Fruitys Tickets"),
        Tier("Tier 5", 1400, "- Discounted Gym Membership"),
        Tier("Tier 6", 1800, "- Library access")
    )

    Scaffold(
        topBar = {
            TopBar(
                points = points,
                multiplier = multiplier,
                onBack = { if (activePage != "home") activePage = "home" },
                onReset = onResetStats
            )
        },
        containerColor = Black
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (activePage) {
                "home" -> HomePage(
                    onNavigate = { activePage = it },
                    onLogout = onLogout
                )
                "faceScan" -> FaceScanPage(onScanSuccess = {
                    onPointsAdded(50 * multiplier)
                    activePage = "home"
                })
                "checkInScreen" -> CheckInPage(
                    user = user,
                    status = checkInStatus,
                    tag = scannedTag,
                    onScanSimulated = onScanSimulated
                )
                "rewards" -> RewardsPage(tierData, points) { cost ->
                    if (points >= cost) onPointsAdded(-cost)
                }
                "analytics" -> AnalyticsPage(points)
            }
        }
    }
}

data class Tier(val label: String, val points: Int, val reward: String)

@Composable
fun TopBar(points: Int, multiplier: Int, onBack: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(LeedsGreen)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LeedsGreen)
            }
            Surface(
                color = Black,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "POINTS: $points",
                    color = PointsColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(LeedsRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("x$multiplier", color = Color.White, fontWeight = FontWeight.Bold)
            }
            // Placeholder for logo
            Box(modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)))

            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(40.dp)
                    .background(LeedsRed, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
            }
        }
    }
}

@Composable
fun HomePage(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Minerva Part 2",
            fontSize = 48.sp,
            color = LeedsGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 40.dp)
        )

        val items = listOf(
            HomeTileData("1", "Face Scan", "faceScan"),
            HomeTileData("2", "Locked", null),
            HomeTileData("3", "Check In", "checkInScreen"),
            HomeTileData("4", "Coming Soon", null),
            HomeTileData("5", "Rewards", "rewards"),
            HomeTileData("6", "Analytics", "analytics")
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for (row in items.chunked(2)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { item ->
                        HomeTile(item, modifier = Modifier.weight(1f)) {
                            item.route?.let { onNavigate(it) }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = onLogout) {
            Text("Logout", color = LeedsRed)
        }
    }
}

data class HomeTileData(val number: String, val label: String, val route: String?)

@Composable
fun HomeTile(data: HomeTileData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val alpha = if (data.route == null) 0.5f else 1f
    Card(
        onClick = onClick,
        enabled = data.route != null,
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = LeedsGreen.copy(alpha = alpha)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, if (data.route != null) PointsColor.copy(alpha = 0.3f) else Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(data.number, fontSize = 48.sp, color = PointsColor, fontWeight = FontWeight.Bold)
            Text(data.label, fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
fun FaceScanPage(onScanSuccess: () -> Unit) {
    var status by remember { mutableStateOf("") }
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            status = "Scanning..."
            delay(2000)
            status = "✓ Scan Successful! +50 Points"
            delay(1000)
            onScanSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Face Scan Check-In", fontSize = 32.sp, color = LeedsGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(LeedsGreen, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.White)
            if (scanning) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).height(4.dp),
                    color = PointsColor,
                    trackColor = Color.Transparent
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { scanning = true },
            colors = ButtonDefaults.buttonColors(containerColor = LeedsGreen),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("START SCAN", fontWeight = FontWeight.Bold)
        }
        Text(status, modifier = Modifier.padding(top = 20.dp), color = LeedsGreen)
    }
}

@Composable
fun CheckInPage(
    user: AuthResponse,
    status: String,
    tag: String?,
    onScanSimulated: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("NFC Check-In", fontSize = 32.sp, color = LeedsGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Logged in as ${user.userName}", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(LeedsGreen.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .border(2.dp, LeedsGreen, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Nfc, 
                contentDescription = null, 
                modifier = Modifier.size(100.dp), 
                tint = if (status.contains("SUCCESS")) PointsColor else LeedsGreen
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = status,
            fontSize = 24.sp,
            color = if (status.contains("SUCCESS")) PointsColor else Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        if (tag != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ID: $tag", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onScanSimulated("COMP2850_LIVE") },
            colors = ButtonDefaults.buttonColors(containerColor = LeedsGreen),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SIMULATE CLASS SCAN", fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Or tap your device against a physical NFC tag",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RewardsPage(tiers: List<Tier>, currentPoints: Int, onBuy: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Reward Tiers", fontSize = 32.sp, color = LeedsGreen, fontWeight = FontWeight.Bold)
        }
        itemsIndexed(tiers) { index, tier ->
            RewardCard(tier, currentPoints >= tier.points, onBuy)
        }
    }
}

@Composable
fun RewardCard(tier: Tier, unlocked: Boolean, onBuy: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) LeedsGreen.copy(alpha = 0.2f) else Black
        ),
        border = BorderStroke(2.dp, if (unlocked) LeedsGreen else MidGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(if (unlocked) LeedsGreen else MidGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(tier.label.last().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(tier.label, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${tier.points} Points", color = PointsColor)
                Text(tier.reward, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Button(
                onClick = { onBuy(tier.points) },
                enabled = unlocked,
                colors = ButtonDefaults.buttonColors(containerColor = LeedsGreen)
            ) {
                Text("BUY")
            }
        }
    }
}

@Composable
fun AnalyticsPage(currentPoints: Int) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Analytics Dashboard", fontSize = 32.sp, color = LeedsGreen, fontWeight = FontWeight.Bold)

        AnalyticsCard("Attendance Over Time") {
            Row(
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val data = listOf(0.6f, 0.8f, 0.45f, 0.9f, 0.7f)
                val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
                data.forEachIndexed { i, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight(value)
                                .background(LeedsGreen, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                        Text(labels[i], color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }

        AnalyticsCard("Points Distribution") {
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val pointsList = listOf(0.1f, 0.3f, 0.4f, 0.6f, 0.8f, 0.9f)
                val path = Path().apply {
                    moveTo(0f, size.height)
                    pointsList.forEachIndexed { i, p ->
                        lineTo(i * (size.width / (pointsList.size - 1)), size.height * (1 - p))
                    }
                }
                drawPath(path, color = LeedsGreen, style = Stroke(width = 4f))
            }
        }

        AnalyticsCard("Leaderboard") {
            val leaderboard = listOf(
                "Student A" to 1250,
                "Student B" to 980,
                "Student C" to 750,
                "Student D" to 620,
                "You" to currentPoints
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                leaderboard.forEachIndexed { i, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Black, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${i + 1}", color = PointsColor, fontWeight = FontWeight.Bold)
                        Text(entry.first, color = Color.White, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                        Text("${entry.second}", color = PointsColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LeedsGreen.copy(alpha = 0.1f)),
        border = BorderStroke(2.dp, LeedsGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = LeedsGreen, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
