package com.sae.wavetime.domain.model

data class Block(
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String = "permanent",
    val penaltyMinutes: Int = 0,
    val isActive: Boolean = true,
    val unlockUntil: Long = 0L,
    val reactivateAt: Long = 0L,
    val isDeleted: Boolean = false,
)