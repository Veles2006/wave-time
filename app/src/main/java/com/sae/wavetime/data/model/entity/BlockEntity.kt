package com.sae.wavetime.data.model.entity

import android.graphics.drawable.Icon
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String = "permanent",
    val penaltyMinutes: Int = 0,
    val isActive: Boolean = true,
    val unlockUntil: Long = 0L,

    @ColumnInfo(defaultValue = "0")
    val reactivateAt: Long = 0L,

    val isDeleted: Boolean = false
)
