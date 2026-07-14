package com.sae.wavetime.ui.common

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiText {

    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText

    data class DynamicString(
        val value: String
    ) : UiText

    data class LocalizedKeyName(
        val value: String
    ) : UiText
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.StringResource -> {
            val localizedArgs = args.map { arg ->
                when (arg) {
                    is UiText -> arg.asString(context)
                    else -> arg
                }
            }

            context.getString(
                resId,
                *localizedArgs.toTypedArray()
            )
        }

        is UiText.DynamicString -> value

        is UiText.LocalizedKeyName -> {
            value.toLocalizedKeyName(context)
        }
    }
}