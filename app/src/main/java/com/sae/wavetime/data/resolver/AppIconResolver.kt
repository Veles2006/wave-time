package com.sae.wavetime.data.resolver

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log

class AppIconResolver(
    private val context: Context
) {
    fun getIcon(packageName: String): Drawable? {
        val pm = context.packageManager

        return try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val icon = pm.getApplicationIcon(appInfo)
            icon

        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}