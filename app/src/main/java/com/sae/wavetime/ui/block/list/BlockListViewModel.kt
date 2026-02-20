package com.sae.wavetime.ui.block.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.AppRepository
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockListViewModel(
    private val repository: AppRepository
): ViewModel() {
    private val _state = MutableStateFlow(BlockListState())
    val state: StateFlow<BlockListState> = _state

    fun loadApps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val apps: List<AppUiModel> = repository.getApps()

                _state.update {
                    it.copy(
                        isLoading = false,
                        apps = apps,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                    )
                }
            }
        }
    }
}