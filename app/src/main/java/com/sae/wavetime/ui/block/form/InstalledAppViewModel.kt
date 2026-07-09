package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InstalledAppViewModel(
    private val installedAppRepo: InstalledAppRepository,
    private val blockRepo: BlockRepository
) : ViewModel() {

    val apps = combine(
        installedAppRepo.getInstalledApps(),
        blockRepo.getBlocksFlow()
    ) { installedApps, blocks ->

        val blockedPackages = blocks
            .map { it.packageName }
            .toSet()

        installedApps
            .filter { app ->
                app.packageName !in blockedPackages
            }
            .map { app ->
                AppUiModel(
                    id = app.packageName,
                    appName = app.appName,
                    packageName = app.packageName,
                    icon = app.icon,
                    isActive = false
                )
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                installedAppRepo.refreshInstalledApps()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
            }
        }
    }
}