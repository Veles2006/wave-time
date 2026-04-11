package com.sae.wavetime.ui.task.create

import com.sae.wavetime.ui.model.RewardSelectUiModel

data class RewardSelectState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val rewards: List<RewardSelectUiModel> = emptyList(),
    val selectedRewards: List<RewardSelectUiModel> = emptyList()
)