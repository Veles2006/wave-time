package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InstalledAppViewModel(
    private val installedAppRepo: InstalledAppRepository,
    private val blockRepo: BlockRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val state = combine(
        installedAppRepo.getInstalledApps(),
        blockRepo.getBlocksFlow(),
        searchQuery
    ) { installedApps, blocks, query ->

        val blockedPackages = blocks
            .map { block -> block.packageName }
            .toSet()

        val normalizedQuery = query.trim()

        val apps = installedApps
            .asSequence()
            .filter { app ->
                app.packageName !in blockedPackages
            }
            .filter { app ->
                normalizedQuery.isBlank() ||
                        app.appName.contains(
                            other = normalizedQuery,
                            ignoreCase = true
                        ) ||
                        app.packageName.contains(
                            other = normalizedQuery,
                            ignoreCase = true
                        )
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
            .toList()

        InstalledAppUiState(
            isLoading = false,
            apps = apps,
            searchQuery = query
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
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InstalledAppUiState(
                isLoading = true
            )
        )

    fun searchApps(query: String) {
        searchQuery.value = query
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                installedAppRepo.refreshInstalledApps()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Có thể cập nhật error state sau
            }
        }
    }
}