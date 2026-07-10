package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InstalledAppViewModel(
    private val installedAppRepo: InstalledAppRepository,
    private val blockRepo: BlockRepository
) : ViewModel() {

    val state = combine(
        installedAppRepo.getInstalledApps(),
        blockRepo.getBlocksFlow()
    ) { installedApps, blocks ->

        val blockedPackages = blocks
            .map { it.packageName }
            .toSet()

        val apps = installedApps
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

        InstalledAppUiState(
            isLoading = false,
            apps = apps
        )
    }
        .catch { e ->
            emit(
                InstalledAppUiState(
                    isLoading = false,
                    error = e.message ?: "Load installed apps failed"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InstalledAppUiState()
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