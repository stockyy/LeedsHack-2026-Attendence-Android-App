package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.app.ui.theme.*

data class RewardTier(
    val tier: Int,
    val points: Int,
    val reward: String
)

val rewardTiers = listOf(
    RewardTier(1, 100, "- Printing Credits\n- Freeze Streak"),
    RewardTier(2, 200, "- Double or Nothing"),
    RewardTier(3, 500, "- GFAL Points"),
    RewardTier(4, 600, "- Fruitys Tickets"),
    RewardTier(5, 1400, "- Discounted Gym Membership"),
    RewardTier(6, 1800, "- Library access")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(currentPoints: Int, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("POINTS: $currentPoints") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Reward Tiers",
                    color = LeedsGreen,
                    fontSize = 32.sp,
                    fontFamily = spaceMonoFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            items(rewardTiers) { tier ->
                RewardTierItem(tier = tier, currentPoints = currentPoints)
            }
        }
    }
}

@Composable
fun RewardTierItem(tier: RewardTier, currentPoints: Int) {
    val unlocked = currentPoints >= tier.points
    // Tier 1 should not light up when you have no points
    val badgeColor = if (unlocked && (tier.tier != 1 || currentPoints > 0)) LeedsGreen else MidGray

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(badgeColor, shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tier.tier.toString(),
                    color = White,
                    fontSize = 32.sp,
                    fontFamily = spaceMonoFamily,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(text = "Tier ${tier.tier}", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "${tier.points} Points", color = if (unlocked) PointsColor else MidGray, fontSize = 12.sp)
                Text(text = tier.reward, color = White, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
        Button(
            onClick = { /* TODO */ },
            enabled = unlocked,
            colors = ButtonDefaults.buttonColors(
                containerColor = LeedsGreen,
                contentColor = White,
                disabledContainerColor = MidGray
            )
        ) {
            Text("Buy")
        }
    }
}
