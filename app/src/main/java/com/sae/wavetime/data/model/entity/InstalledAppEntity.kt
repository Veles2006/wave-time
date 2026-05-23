package com.sae.wavetime.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_apps")
data class InstalledAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val iconPath: String? = null,
    val isLaunchable: Boolean,
    val lastUpdated: Long
)