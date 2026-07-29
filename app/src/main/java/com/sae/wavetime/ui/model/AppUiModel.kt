package com.sae.wavetime.ui.model

import android.graphics.drawable.Drawable

data class AppUiModel(
    val id: String,
    val appName: String,
    val packageName: String,
    val blockType: String = "permanent",
    val icon: Drawable? = null,
    val unlockUntil: Long = 0L,
    val reactivateAt: Long = 0L,
    val penaltyMinutes: Int = 0,
    val isActive: Boolean = true
)