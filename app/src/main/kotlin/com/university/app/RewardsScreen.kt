package com.university.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.university.app.network.RewardTier
import com.university.app.ui.theme.LeedsGreen
import com.university.app.ui.theme.White
import com.university.app.ui.theme.spaceMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    studentId: Int, // <--- ADDED THIS PARAMETER
    rewardsViewModel: RewardsViewModel = viewModel(),
    onBack: () -> Unit
) {
    val rewardTiers by rewardsViewModel.rewardTiers.collectAsState()

    // Pass studentId to the fetch function
    LaunchedEffect(Unit) {
        rewardsViewModel.getRewardTiers(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewards", fontFamily = spaceMonoFamily, color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reward Tiers",
                color = LeedsGreen,
                fontSize = 56.sp,
                fontFamily = spaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(rewardTiers) { tier ->
                    RewardTierItem(tier)
                }
            }
        }
    }
}

@Composable
fun RewardTierItem(tier: RewardTier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (tier.unlocked) LeedsGreen else Color.Gray),
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
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Tier ${tier.tier}",
            color = White,
            fontSize = 14.sp,
            fontFamily = spaceMonoFamily,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = tier.reward,
            color = White,
            fontSize = 11.sp,
            fontFamily = spaceMonoFamily,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (tier.unlocked) LeedsGreen else Color.Gray,
                contentColor = White
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Buy", fontSize = 12.sp, fontFamily = spaceMonoFamily, fontWeight = FontWeight.Bold)
        }
    }
}