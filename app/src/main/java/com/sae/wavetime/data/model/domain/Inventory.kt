package com.sae.wavetime.data.model.domain

data class Inventory(
    val id: String,
    val itemId: Item,
    val quantity: Int,
)
