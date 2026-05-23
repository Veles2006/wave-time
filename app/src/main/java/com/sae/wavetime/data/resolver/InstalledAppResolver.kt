package com.sae.wavetime.data.resolver

import android.content.Context
import android.content.pm.PackageManager
import com.sae.wavetime.ui.model.AppUiModel

class InstalledAppResolver(
    private val context: Context,
) {
    fun getInstalledApps(blocks: List<AppUiModel>): List<AppUiModel> {
        val pm = context.packageManager
        val currentPackageName = context.packageName
        val blockedPackages = blocks.map { it.packageName }.toSet()

        val result = runCatching {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }
            .getOrDefault(emptyList())
            .mapNotNull { appInfo ->
                val packageName = appInfo.packageName

                if (packageName == currentPackageName) return@mapNotNull null
                if (packageName in blockedPackages) return@mapNotNull null
                if (pm.getLaunchIntentForPackage(packageName) == null) return@mapNotNull null

                AppUiModel(
                    id = packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    packageName = packageName,
                    icon = runCatching {
                        pm.getApplicationIcon(appInfo)
                    }.getOrNull()
                )
            }
            .sortedBy { it.appName.lowercase() }

        return result
    }
}