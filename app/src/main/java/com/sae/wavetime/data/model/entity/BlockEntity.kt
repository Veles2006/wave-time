package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String,
    val penaltyMinutes: Int,
    val isActive: Boolean,
)
