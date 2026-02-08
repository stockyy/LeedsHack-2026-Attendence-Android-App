package com.university.app

import androidx.lifecycle.ViewModel
import com.university.app.network.LeaderboardItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnalyticsViewModel : ViewModel() {

    private val _leaderboardItems = MutableStateFlow<List<LeaderboardItem>>(emptyList())
    val leaderboardItems: StateFlow<List<LeaderboardItem>> = _leaderboardItems

    fun getLeaderboard() {
        _leaderboardItems.value = listOf(
            LeaderboardItem(rank = 1, name = "John Doe", score = "1500 XP"),
            LeaderboardItem(rank = 2, name = "Jane Smith", score = "1450 XP"),
            LeaderboardItem(rank = 3, name = "Peter Jones", score = "1300 XP"),
            LeaderboardItem(rank = 4, name = "Sarah Miller", score = "1250 XP"),
            LeaderboardItem(rank = 5, name = "Michael Brown", score = "1200 XP"),
            LeaderboardItem(rank = 6, name = "Emily Davis", score = "1100 XP"),
            LeaderboardItem(rank = 7, name = "David Wilson", score = "1050 XP"),
            LeaderboardItem(rank = 8, name = "Jessica Taylor", score = "1000 XP"),
            LeaderboardItem(rank = 9, name = "Chris Green", score = "950 XP"),
            LeaderboardItem(rank = 10, name = "Laura Harris", score = "900 XP")
        )
    }
}