package com.sae.wavetime.ui.block.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.domain.usecase.ActivateBlockUseCase
import com.sae.wavetime.domain.usecase.DeleteBlockUseCase
import com.sae.wavetime.domain.usecase.TemporarilyDeactivateBlockUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockListViewModel(
    private val blockRepo: BlockRepository,
    private val activateBlockUseCase: ActivateBlockUseCase,
    private val temporarilyDeactivateBlockUseCase: TemporarilyDeactivateBlockUseCase,
    private val deleteBlockUseCase: DeleteBlockUseCase
): ViewModel() {

    private val _state = MutableStateFlow(BlockListState())
    val state: StateFlow<BlockListState> = _state

    init {
        loadBlocks()
    }

    private fun loadBlocks() {
        viewModelScope.launch {
            blockRepo.getBlocksFlow()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { blocks ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            blocks = blocks
                        )
                    }
                }
        }
    }

    fun setActive(id: String, isChecked: Boolean) {
        viewModelScope.launch {
            blockRepo.setActive(id, isChecked)
        }
    }

    fun changeBlockActive(
        blockId: String,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            if (isActive) {
                activateBlockUseCase.execute(blockId)
            } else {
                temporarilyDeactivateBlockUseCase.execute(blockId)
            }
        }
    }

    // Delete method
    fun softDelete(id: String) {
        viewModelScope.launch {
            deleteBlockUseCase.execute(id)
        }
    }
}