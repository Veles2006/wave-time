package com.sae.wavetime.domain.model


data class BlockOfItem(
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
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val keyInfo: KeyInfoPopulated,
    val description: String,
    val icon: String,
)

