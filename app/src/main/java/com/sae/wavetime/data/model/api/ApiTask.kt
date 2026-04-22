package com.sae.wavetime.data.model.api

import com.google.gson.annotations.SerializedName

data class ApiRewardItemId(
    @SerializedName("_id")
    val id: String,

    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val description: String,
    val icon: String,
)


data class ApiRewardItem(
    val itemId: ApiRewardItemId,
    val quantity: Int,
)

data class ApiReward(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
    val items: List<ApiRewardItem> = emptyList(),
)

data class ApiPenalty(
    val exp: Int = 0,
    val gold: Int = 0,
    val diamond: Int = 0,
    val gem: Int = 0,
)

data class ApiTask(
    @SerializedName("_id")
    val id: String,

    val name: String,
    val description: String?,
    val status: String,
    val reward: ApiReward,
    val penalty: ApiPenalty,
    val deadline: String? = null,
    val date: String? = null,
    val difficulty: String,
    val isDeleted: Boolean = false
)
