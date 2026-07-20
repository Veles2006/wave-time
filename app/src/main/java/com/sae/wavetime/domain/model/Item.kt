package com.sae.wavetime.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class BlockOfItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("appName")
    val appName: String,

    @SerializedName("packageName")
    val packageName: String,

    @SerializedName("blockType")
    val blockType: String,
)

@Keep
data class KeyInfoPopulated(
    @SerializedName("blockId")
    val blockId: BlockOfItem?,

    @SerializedName("isMaster")
    val isMaster: Boolean,

    @SerializedName("durationMinutes")
    val durationMinutes: Int = 0,
)

data class Item(
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val keyInfo: KeyInfoPopulated,
    val description: String,
)

