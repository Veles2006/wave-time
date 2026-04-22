package com.sae.wavetime.data.model.domain

data class RewardItemId(
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val description: String,
    val icon: String,
)

data class RewardItem(
    val itemId: RewardItemId,
    val quantity: Int,
)

data class Reward(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
    val items: List<RewardItem> = emptyList(),
)

data class Penalty(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
)

data class Task(
    val id: String,
    val name: String,
    val description: String?,
    val status: String,
    val reward: Reward,
    val penalty: Penalty,
    val deadline: String? = null,
    val date: String? = null,
    val difficulty: String,
    val isDeleted: Boolean = false
)
