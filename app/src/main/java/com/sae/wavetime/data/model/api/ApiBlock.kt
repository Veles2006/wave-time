package com.sae.wavetime.data.model.api

import com.google.gson.annotations.SerializedName

data class ApiBlock(
    @SerializedName("_id")
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String,
    val penaltyMinutes: Int,
    val isActive: Boolean,
)

data class BlockRequest(
    val appName: String,
    val packageName: String,
    val blockType: String,
)

