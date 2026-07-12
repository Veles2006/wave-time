package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory",
    indices = [
        Index(value = ["itemId"], unique = true)
    ]
)
data class InventoryEntity(
    @PrimaryKey
    val id: String,
    val itemId: String,
    val quantity: Int
)