package com.sae.wavetime.ui.common

import android.content.Context
import com.sae.wavetime.R
import java.util.Locale

fun String.toStatusText(context: Context): String {
    return when (this) {
        "pending" -> context.getString(R.string.status_pending)
        "completed" -> context.getString(R.string.status_completed)
        "in_progress" -> context.getString(R.string.status_in_progress)
        else -> this
    }
}

fun String.toTaskTypeText(context: Context): String {
    return when (this) {
        "default" -> context.getString(R.string.task_type_default_value)
        "daily" -> context.getString(R.string.task_type_daily_value)
        else -> this
    }
}

fun String.toCompleteModeText(context: Context): String {
    return when (this) {
        "tap" -> context.getString(R.string.complete_mode_tap)
        "timer" -> context.getString(R.string.complete_mode_timer)
        else -> this
    }
}

fun String.toDifficultyText(context: Context): String {
    return when (this.lowercase(Locale.ROOT)) {
        "mortal" -> context.getString(R.string.difficulty_mortal)
        "yao" -> context.getString(R.string.difficulty_yao)
        "gui" -> context.getString(R.string.difficulty_gui)
        "mara" -> context.getString(R.string.difficulty_mara)
        "sage" -> context.getString(R.string.difficulty_sage)
        "xian" -> context.getString(R.string.difficulty_xian)
        "deity" -> context.getString(R.string.difficulty_deity)
        "creation" -> context.getString(R.string.difficulty_creation)
        else -> this
    }
}

fun Int.toDifficultyText(context: Context): String {
    return when (this) {
        1 -> context.getString(R.string.difficulty_mortal)
        2 -> context.getString(R.string.difficulty_yao)
        3 -> context.getString(R.string.difficulty_gui)
        4 -> context.getString(R.string.difficulty_mara)
        5 -> context.getString(R.string.difficulty_sage)
        6 -> context.getString(R.string.difficulty_xian)
        7 -> context.getString(R.string.difficulty_deity)
        8 -> context.getString(R.string.difficulty_creation)
        else -> this.toString()
    }
}

fun Long?.toHourMinuteSecond(context: Context): String {
    if (this == null) return context.getString(R.string.status_pending)

    val date = java.util.Date(this)
    val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    return formatter.format(date)
}