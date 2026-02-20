package com.sae.wavetime.data.repository

import android.content.Context
import androidx.core.content.ContextCompat
import com.sae.wavetime.R
import com.sae.wavetime.ui.model.AppUiModel

class AppRepository(
    private val context: Context
) {

    suspend fun getApps(): List<AppUiModel> {
        return listOf(
            AppUiModel(
                appName = "Youtube",
                packageName = "com.google.android.youtube",
                icon = ContextCompat.getDrawable(
                    context,
                    R.drawable.waifu_1
                )!!
            )
        )
    }
}