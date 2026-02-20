package com.sae.wavetime.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RewardItemId(
    @SerializedName("_id")
    val id: String,

    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val description: String,
    val icon: String,
) : Parcelable

@Parcelize
data class RewardItem(
    val itemId: RewardItemId,
    val quantity: Int,
) : Parcelable

@Parcelize
data class Reward(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
    val items: List<RewardItem> = emptyList(),
) : Parcelable

@Parcelize
data class Penalty(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
) : Parcelable

data class Task(
    @SerializedName("_id")
    val id: String,

    val name: String,
    val description: String?,
    val reward: Reward,
    val penalty: Penalty,
    val deadline: String?,
    val date: String?,
    val difficulty: String,
)
