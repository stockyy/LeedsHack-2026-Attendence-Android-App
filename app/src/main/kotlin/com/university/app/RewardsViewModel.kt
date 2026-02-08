package com.university.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.university.app.network.ApiClient
import com.university.app.network.RewardTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {

    private val _rewardTiers = MutableStateFlow<List<RewardTier>>(emptyList())
    val rewardTiers: StateFlow<List<RewardTier>> = _rewardTiers

    // Updated to accept studentId
    fun getRewardTiers(studentId: Int) {
        viewModelScope.launch {
            try {
                _rewardTiers.value = ApiClient.getRewardTiers(studentId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}