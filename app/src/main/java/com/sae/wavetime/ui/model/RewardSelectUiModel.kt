package com.sae.wavetime.ui.model

data class RewardSelectUiModel(
    val id: String,
    val name: String,
    val tier: String,
    val quantity: Int = 0
)
