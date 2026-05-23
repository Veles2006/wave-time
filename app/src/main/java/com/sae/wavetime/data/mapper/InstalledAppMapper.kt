package com.sae.wavetime.data.mapper

import com.sae.wavetime.data.model.entity.InstalledAppEntity
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.ui.model.AppUiModel

fun InstalledAppEntity.toUi(
    iconResolver: AppIconResolver
): AppUiModel {
    return AppUiModel(
        id = packageName,
        appName = appName,
        packageName = packageName,
        icon = iconResolver.getIcon(packageName)
    )
}

fun AppUiModel.toInstalledAppEntity(): InstalledAppEntity {
    return InstalledAppEntity(
        packageName = packageName,
        appName = appName,
        iconPath = null,
        isLaunchable = true,
        lastUpdated = System.currentTimeMillis()
    )
}