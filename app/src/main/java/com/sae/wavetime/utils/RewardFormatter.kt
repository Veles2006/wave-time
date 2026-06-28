package com.sae.wavetime.utils

import android.content.Context
import com.sae.wavetime.R
import com.sae.wavetime.domain.model.Reward
import com.sae.wavetime.domain.model.Penalty

fun Reward.toDisplayString(context: Context): String {
    val parts = mutableListOf<String>()

    if (gold > 0) parts.add("💰 $gold")
    if (exp > 0) parts.add("⭐ $exp")
    if (diamond > 0) parts.add("💎 $diamond")
    if (gem > 0) parts.add("🔷 $gem")

    if (items.isNotEmpty()) {
        val itemText = items.joinToString(", ") {
            "📦 ${it.itemId.name} x${it.quantity}"
        }
        parts.add(itemText)
    }

    return if (parts.isEmpty()) context.getString(R.string.no_reward) else parts.joinToString(" • ")
}

fun Penalty.toDisplayString(context: Context): String {
    val parts = mutableListOf<String>()

    if (gold > 0) parts.add("-💰 $gold")
    if (exp > 0) parts.add("-⭐ $exp")
    if (diamond > 0) parts.add("-💎 $diamond")
    if (gem > 0) parts.add("-🔷 $gem")

    return if (parts.isEmpty()) context.getString(R.string.no_penalty) else parts.joinToString(" • ")
}