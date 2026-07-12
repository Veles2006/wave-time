package com.sae.wavetime.ui.model

data class InventoryDetailUiModel(
    val inventoryId: String,
    val itemId: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val blockId: String?,
    val blockName: String? = null,
    val durationMinutes: Int,
    val isMaster: Boolean,
    val description: String,
    val quantity: Int,
)
