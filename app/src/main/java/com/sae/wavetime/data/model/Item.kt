package com.sae.wavetime.data.model

import com.google.gson.annotations.SerializedName

data class BlockOfItem(
    @SerializedName("_id")
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String,
)
data class KeyInfoPopulated(
    val blockId: BlockOfItem?,
    val isMaster: Boolean,
)

data class Item(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val keyInfo: KeyInfoPopulated,
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

data class Inventory(
    @SerializedName("_id")
    val id: String,
    val itemId: Item,
    val quantity: Int,
)

data class InventoryItemRequest(
    val itemId: String,
    val amount: Int,
)

data class InventoryBulkRequest(
    val items: List<InventoryItemRequest>,
)

data class MessageResponse(
    val message: String,
    val count: Int,
)