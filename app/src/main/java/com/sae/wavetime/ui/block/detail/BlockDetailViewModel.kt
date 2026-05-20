package com.sae.wavetime.ui.block.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.ui.block.form.BlockFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockDetailViewModel(
    private val blockRepo: BlockRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BlockDetailState())
    val state: StateFlow<BlockDetailState> = _state

    fun observeBlock(id: String) {
        viewModelScope.launch {
            blockRepo.getBlockByIdFlow(id)
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
                            block = block,
                            error = if (block == null) "Block not found" else null
                        )
                    }
                }
        }
    }

    fun softDelete(id: String) {
        viewModelScope.launch {
            blockRepo.softDelete(id)
        }
    }
}