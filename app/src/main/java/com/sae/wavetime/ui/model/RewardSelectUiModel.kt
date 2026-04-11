package com.sae.wavetime.ui.model

data class RewardSelectUiModel(
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val description: String,
    val icon: String,
    val quantity: Int = 0
)
