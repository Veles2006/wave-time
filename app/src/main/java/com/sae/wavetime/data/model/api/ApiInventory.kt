package com.sae.wavetime.data.model.api

import com.google.gson.annotations.SerializedName

data class ApiInventory(
    @SerializedName("_id")
    val id: String,
    val itemId: ApiItem,
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
