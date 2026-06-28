package com.sae.wavetime.ui.common

import android.content.Context
import com.sae.wavetime.R
import java.util.Locale

fun String.toBlockTypeText(context: Context): String {
    return when (this.lowercase(Locale.ROOT)) {
        "permanent" -> context.getString(R.string.block_type_permanent)
        "schedule" -> context.getString(R.string.block_type_schedule)
        "timer" -> context.getString(R.string.block_type_timer)
        else -> this
    }
}