package com.sae.wavetime.data.model

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class Block(
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
