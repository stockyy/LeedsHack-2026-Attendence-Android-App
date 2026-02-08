package com.university.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.university.app.network.ApiClient
import com.university.app.network.LeaderboardItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {

    private val _leaderboardItems = MutableStateFlow<List<LeaderboardItem>>(emptyList())
    val leaderboardItems: StateFlow<List<LeaderboardItem>> = _leaderboardItems

    fun getLeaderboard() {
        viewModelScope.launch {
            _leaderboardItems.value = ApiClient.getLeaderboard()
        }
    }
}