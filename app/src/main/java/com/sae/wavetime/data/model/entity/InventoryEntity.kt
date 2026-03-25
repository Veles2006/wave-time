package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val quantity: Int
)