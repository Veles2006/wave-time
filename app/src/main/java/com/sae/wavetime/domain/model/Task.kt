package com.sae.wavetime.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RewardItemId(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("tier")
    val tier: String,

    @SerializedName("rank")
    val rank: Int,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String,
)

@Keep
data class RewardItem(
    @SerializedName("itemId")
    val itemId: RewardItemId,

    @SerializedName("quantity")
    val quantity: Int,
)

@Keep
data class Reward(
    @SerializedName("exp")
    val exp: Int = 0,

    @SerializedName("gold")
    val gold: Int = 0,

    @SerializedName("diamond")
    val diamond: Int = 0,

    @SerializedName("gem")
    val gem: Int = 0,

    @SerializedName("items")
    val items: List<RewardItem> = emptyList(),
)

@Keep
data class Penalty(
    @SerializedName("exp")
    val exp: Int = 0,

    @SerializedName("gold")
    val gold: Int = 0,

    @SerializedName("diamond")
    val diamond: Int = 0,

    @SerializedName("gem")
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
