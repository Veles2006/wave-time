package com.sae.wavetime.ui.model

import android.graphics.drawable.Drawable

data class AppUiModel(
    val id: String,
    val appName: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isActivity: Boolean = false
)