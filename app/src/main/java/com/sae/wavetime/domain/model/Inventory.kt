package com.sae.wavetime.domain.model

data class Inventory(
    val id: String,
    val itemId: Item,
    val quantity: Int,
)