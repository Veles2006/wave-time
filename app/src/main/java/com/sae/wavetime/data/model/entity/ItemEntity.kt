package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sae.wavetime.domain.model.KeyInfoPopulated

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val tier: String,
    val rank: Int,
    val category: String,
    val keyInfo: KeyInfoPopulated,
    val description: String,
    val icon: String,
)
