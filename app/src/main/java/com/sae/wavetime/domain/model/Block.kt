package com.sae.wavetime.domain.model

data class Block(
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String,
    val penaltyMinutes: Int,
    val isActive: Boolean,
)