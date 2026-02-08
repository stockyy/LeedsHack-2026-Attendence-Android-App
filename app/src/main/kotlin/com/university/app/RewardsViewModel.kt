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

    fun getRewardTiers() {
        viewModelScope.launch {
            _rewardTiers.value = ApiClient.getRewardTiers()
        }
    }
}