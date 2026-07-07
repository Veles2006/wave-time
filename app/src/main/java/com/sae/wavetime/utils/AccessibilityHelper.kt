package com.sae.wavetime.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast

fun Context.openAccessibilitySettings() {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            this,
            "Không thể mở cài đặt Trợ năng trên thiết bị này",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.isAccessibilityServiceEnabled(
    serviceClass: Class<out AccessibilityService>
): Boolean {
    val expectedComponent = ComponentName(this, serviceClass)

    val accessibilityManager =
        getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
        AccessibilityServiceInfo.FEEDBACK_ALL_MASK
    )

    return enabledServices.any { serviceInfo ->
        val resolveInfo = serviceInfo.resolveInfo.serviceInfo
        val enabledComponent = ComponentName(
            resolveInfo.packageName,
            resolveInfo.name
        )

        enabledComponent == expectedComponent
    }
}