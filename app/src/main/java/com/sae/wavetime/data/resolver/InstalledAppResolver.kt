package com.sae.wavetime.data.resolver

import android.content.Context
import android.content.pm.PackageManager
import com.sae.wavetime.ui.model.AppUiModel
import java.util.UUID

class InstalledAppResolver(
    private val context: Context,
) {
    fun getInstalledApps(
        blocks: List<AppUiModel>
    ): List<AppUiModel> {
        val pm = context.packageManager
        val currentPackageName = context.packageName

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { appInfo ->
                val packageName = appInfo.packageName

                val isCurrentApp = packageName == currentPackageName
                val isNotLaunchable = pm.getLaunchIntentForPackage(packageName) == null
                val isAlreadyBlocked = blocks.any { block ->
                    block.packageName == packageName
                }

                if (isCurrentApp || isNotLaunchable || isAlreadyBlocked) {
                    return@mapNotNull null
                }

                AppUiModel(
                    id = UUID.randomUUID().toString(),
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    packageName = packageName,
                    icon = pm.getApplicationIcon(appInfo)
                )
            }
            .sortedBy {
                it.appName.lowercase()
            }
    }
}