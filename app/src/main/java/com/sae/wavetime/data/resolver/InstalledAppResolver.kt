package com.sae.wavetime.data.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.sae.wavetime.ui.model.AppUiModel

class InstalledAppResolver(
    private val context: Context,
) {

    fun getInstalledApps(): List<AppUiModel> {
        val packageManager = context.packageManager
        val currentPackageName = context.packageName

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launcherApps = runCatching {
            queryLauncherActivities(
                packageManager = packageManager,
                launcherIntent = launcherIntent,
            )
        }.getOrDefault(emptyList())

        return launcherApps
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo
                    ?: return@mapNotNull null

                val packageName = activityInfo.packageName

                // Không hiển thị chính Wave Time trong danh sách chặn
                if (packageName == currentPackageName) {
                    return@mapNotNull null
                }

                AppUiModel(
                    id = packageName,
                    appName = runCatching {
                        resolveInfo.loadLabel(packageManager).toString()
                    }.getOrDefault(packageName),
                    packageName = packageName,
                    icon = runCatching {
                        resolveInfo.loadIcon(packageManager)
                    }.getOrNull(),
                )
            }
            // Một ứng dụng có thể có nhiều launcher activity
            .distinctBy { app ->
                app.packageName
            }
            .sortedBy { app ->
                app.appName.lowercase()
            }
    }

    private fun queryLauncherActivities(
        packageManager: PackageManager,
        launcherIntent: Intent,
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.ResolveInfoFlags.of(0L),
        )
    } else {
        @Suppress("DEPRECATION")
        packageManager.queryIntentActivities(
            launcherIntent,
            0,
        )
    }
}