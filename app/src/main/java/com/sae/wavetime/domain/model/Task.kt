package com.sae.wavetime.domain.model

data class RewardItemId(
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val description: String,
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
    val type: String = "default",
    val completeMode: String = "tap",
    val reward: Reward,
    val penalty: Penalty,
    val requiredDurationMinutes: Int? = null,
    val startedAt: Long? = null,
    val finishAt: Long? = null,
    val deadline: String? = null,
    val date: String? = null,
    val difficulty: String,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastCompletedAt: Long? = null
)
