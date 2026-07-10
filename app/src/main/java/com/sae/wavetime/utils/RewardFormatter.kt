package com.sae.wavetime.utils

import android.content.Context
import com.sae.wavetime.R
import com.sae.wavetime.domain.model.Reward
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.ui.common.toTierText
import java.util.Locale

fun Reward.toDisplayString(context: Context): String {
    val parts = mutableListOf<String>()

    if (gold > 0) parts.add("+ $gold Coin")
    if (exp > 0) parts.add("+ $exp EXP")
    if (diamond > 0) parts.add("💎 $diamond")
    if (gem > 0) parts.add("🔷 $gem")

    items.forEach {
        val itemName = it.itemId.name.toLocalizedKeyName(context)
        parts.add("+ $itemName x${it.quantity}")
    }

    return if (parts.isEmpty()) {
        context.getString(R.string.no_reward)
    } else {
        parts.joinToString("\n")
    }
}

fun Penalty.toDisplayString(context: Context): String {
    val parts = mutableListOf<String>()

    if (gold > 0) parts.add("-💰 $gold")
    if (exp > 0) parts.add("-⭐ $exp")
    if (diamond > 0) parts.add("-💎 $diamond")
    if (gem > 0) parts.add("-🔷 $gem")

    return if (parts.isEmpty()) context.getString(R.string.no_penalty) else parts.joinToString(" • ")
}

fun String.toLocalizedKeyName(context: Context): String {
    val parts = this.split("·").map { it.trim() }

    val keyPart = parts.getOrNull(0) ?: return this
    val appName = parts.getOrNull(1)

    val tier = keyPart
        .removeSuffix("Key")
        .trim()
        .lowercase(Locale.ROOT)

    val tierText = tier.toTierText(context)

    val keyName = context.getString(R.string.item_key_format, tierText)

    return if (appName.isNullOrBlank()) {
        keyName
    } else {
        "$keyName · $appName"
    }
}