package com.sae.wavetime.data.model.api

import com.google.gson.annotations.SerializedName

data class ApiBlockOfItem(
    @SerializedName("_id")
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String,
)
data class ApiKeyInfoPopulated(
    val blockId: ApiBlockOfItem?,
    val isMaster: Boolean,
)

data class ApiItem(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val keyInfo: ApiKeyInfoPopulated,
    val description: String,
    val icon: String,
)

data class ItemIdRequest(
    val itemId: String,
)

data class ItemIdResponse(
    val message: String,
    val quantity: Int,
)


