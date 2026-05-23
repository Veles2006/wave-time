package com.sae.wavetime.ui.block.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.domain.usecase.CreateBlockWithKeyUseCase
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockFormViewModel(
    private val repository: BlockRepository,
    private val createBlockWithKeyUseCase: CreateBlockWithKeyUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BlockFormState())
    val state: StateFlow<BlockFormState> = _state

    fun observeBlock(id: String) {
        viewModelScope.launch {
            repository.getBlockByIdFlow(id)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { block ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedApp = block,
                            error = if (block == null) "Block not found" else null
                        )
                    }
                }
        }
    }
    fun setSelectedApp(app: AppUiModel) {
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        selectedApp = app,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveBlock(block: Block) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                createBlockWithKeyUseCase(block)

                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}