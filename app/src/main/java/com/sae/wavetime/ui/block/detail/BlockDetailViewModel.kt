package com.sae.wavetime.ui.block.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BlockDetailViewModel(
    private val blockRepo: BlockRepository
) : ViewModel() {
    private var timerJob: Job? = null
    private val _state = MutableStateFlow(BlockDetailState())
    val state: StateFlow<BlockDetailState> = _state

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return "%02d:%02d".format(minutes, seconds)
    }

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

                    if (block != null) {
                        startTimer(block.unlockUntil)
                    }
                }
        }
    }

    fun softDelete(id: String) {
        viewModelScope.launch {
            blockRepo.softDelete(id)
        }
    }

    fun startTimer(unlockUntil: Long) {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (isActive) {
                val remainingMillis = unlockUntil - System.currentTimeMillis()

                if (remainingMillis <= 0) {
                    _state.update {
                        it.copy(
                            remainingTime = "00:00",
                            isUnlocked = false
                        )
                    }
                    break
                }

                _state.update {
                    it.copy(
                        remainingTime = formatTime(remainingMillis),
                        isUnlocked = true
                    )
                }

                delay(1000)
            }
        }
    }
}