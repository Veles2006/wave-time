package com.sae.wavetime.utils

import com.sae.wavetime.data.model.api.Reward
import com.sae.wavetime.data.model.api.Penalty

fun Reward.toDisplayString(): String {
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

    return if (parts.isEmpty()) "No reward" else parts.joinToString(" • ")
}

fun Penalty.toDisplayString(): String {
    val parts = mutableListOf<String>()

    if (gold > 0) parts.add("-💰 $gold")
    if (exp > 0) parts.add("-⭐ $exp")
    if (diamond > 0) parts.add("-💎 $diamond")
    if (gem > 0) parts.add("-🔷 $gem")

    return if (parts.isEmpty()) "No penalty" else parts.joinToString(" • ")
}