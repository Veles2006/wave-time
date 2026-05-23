package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.InstalledAppRepository
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InstalledAppViewModel(
    private val repo: InstalledAppRepository
) : ViewModel() {

    val apps = repo.getInstalledApps()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun refresh(blocks: List<AppUiModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.refreshInstalledApps(blocks)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
            }
        }
    }
}