package com.sae.wavetime.data.repository

import android.util.Log
import com.sae.wavetime.data.dao.InstalledAppDao
import com.sae.wavetime.data.mapper.toInstalledAppEntity
import com.sae.wavetime.data.mapper.toUi
import com.sae.wavetime.data.resolver.AppIconResolver
import com.sae.wavetime.data.resolver.InstalledAppResolver
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException

class InstalledAppRepository(
    private val dao: InstalledAppDao,
    private val resolver: InstalledAppResolver,
    private val iconResolver: AppIconResolver
) {
    fun getInstalledApps(): Flow<List<AppUiModel>> {
        return dao.getAll().map { list ->
            list.map { entity ->
                entity.toUi(iconResolver)
            }
        }
    }


    suspend fun refreshInstalledApps() {
        val apps = resolver.getInstalledApps()
            .map { it.toInstalledAppEntity() }
        try {
            dao.clearAll()
            dao.upsertAll(apps)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("InstalledApps", "Refresh failed", e)
        }
    }
}