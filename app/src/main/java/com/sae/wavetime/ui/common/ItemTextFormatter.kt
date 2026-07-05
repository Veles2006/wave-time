package com.sae.wavetime.ui.common

import android.content.Context
import com.sae.wavetime.R

fun String.toCleanItemName(): String {
    return if (contains("Key", ignoreCase = true) && contains("·")) {
        substringAfterLast("·").trim()
    } else {
        this.trim()
    }
}

fun String.toTierText(context: Context): String {
    return when (this.lowercase()) {
        "white" -> context.getString(R.string.tier_white)
        "gray" -> context.getString(R.string.tier_gray)
        "green" -> context.getString(R.string.tier_green)
        "blue" -> context.getString(R.string.tier_blue)
        "purple" -> context.getString(R.string.tier_purple)
        "yellow" -> context.getString(R.string.tier_yellow)
        "red" -> context.getString(R.string.tier_red)
        "black" -> context.getString(R.string.tier_black)
        else -> this
    }
}