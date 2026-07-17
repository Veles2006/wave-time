package com.sae.wavetime.ui.common

import android.content.Context
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.InventoryDetailUiModel
import java.util.Locale

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

fun String.toTierInt(): Int {
    return when (this.lowercase()) {
        "white" -> 1
        "gray" -> 2
        "green" -> 3
        "blue" -> 4
        "purple" -> 5
        "yellow" -> 6
        "red" -> 7
        "black" -> 8
        else -> 1
    }
}

fun String.toKeyName(context: Context): String {
    val tierText = this.toTierText(context)
    return context.getString(R.string.item_key_format, tierText)
}

fun String.toKeyTranslate(context: Context): String {
    return if (this == "key") {
        context.getString(R.string.item_category_key)
    } else {
        this
    }
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

fun InventoryDetailUiModel.getLocalizedDescription(
    context: Context,
): String {
    return when (category) {
        "key" -> context.getString(
            R.string.item_key_description_format,
            blockName.orEmpty(),
        )

        else -> context.getString(
            R.string.item_description_format,
            description
        )
    }
}