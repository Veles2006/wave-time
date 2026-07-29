package com.sae.wavetime.ui.block.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.domain.usecase.DeleteBlockUseCase
import com.sae.wavetime.ui.model.AppUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockDetailViewModel(
    private val blockRepo: BlockRepository,
    private val deleteBlockUseCase: DeleteBlockUseCase
) : ViewModel() {
    private var timerJob: Job? = null
    private val _state = MutableStateFlow(BlockDetailState())
    val state: StateFlow<BlockDetailState> = _state

    private fun formatTime(millis: Long): String {
        val totalSeconds =
            ((millis + 999L) / 1000L)
                .coerceAtLeast(0L)

        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L

        return "%02d:%02d:%02d".format(
            hours,
            minutes,
            seconds
        )
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
                        startTimer(block)
                    } else {
                        stopTimer()
                    }
                }
        }
    }

    fun softDelete(id: String) {
        viewModelScope.launch {
            deleteBlockUseCase.execute(id)
        }
    }

    private fun startTimer(block: AppUiModel) {
        timerJob?.cancel()

        val now = System.currentTimeMillis()

        val countdownMode: BlockCountdownMode
        val deadline: Long

        when {
            /*
             * Block bị tắt tạm thời và đang chờ tự active lại.
             */
            !block.isActive &&
                    block.reactivateAt > now -> {

                countdownMode =
                    BlockCountdownMode.REACTIVATION

                deadline = block.reactivateAt
            }

            /*
             * Block đang active nhưng được Item mở khóa tạm thời.
             */
            block.isActive &&
                    block.unlockUntil > now -> {

                countdownMode =
                    BlockCountdownMode.TEMPORARY_UNLOCK

                deadline = block.unlockUntil
            }

            else -> {
                stopTimer()
                return
            }
        }

        timerJob = viewModelScope.launch {
            while (true) {
                val remainingMillis =
                    (deadline - System.currentTimeMillis())
                        .coerceAtLeast(0L)

                _state.update {
                    it.copy(
                        remainingTime =
                            formatTime(remainingMillis),

                        countdownMode =
                            countdownMode,

                        isUnlocked =
                            countdownMode ==
                                    BlockCountdownMode.TEMPORARY_UNLOCK &&
                                    remainingMillis > 0L
                    )
                }

                Log.d(
                    TAG,
                    "Block countdown: " +
                            "id=${block.id}, " +
                            "mode=$countdownMode, " +
                            "remaining=${formatTime(remainingMillis)}"
                )

                if (remainingMillis <= 0L) {
                    break
                }

                delay(
                    minOf(
                        1_000L,
                        remainingMillis
                    )
                )
            }

            _state.update {
                it.copy(
                    remainingTime = "00:00:00",
                    countdownMode = BlockCountdownMode.NONE,
                    isUnlocked = false
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null

        _state.update {
            it.copy(
                remainingTime = "00:00:00",
                countdownMode = BlockCountdownMode.NONE,
                isUnlocked = false
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    companion object {
        private const val TAG = "BlockDetailViewModel"
    }
}