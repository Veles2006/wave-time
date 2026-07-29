package com.sae.wavetime.ui.block.detail

import com.sae.wavetime.ui.model.AppUiModel

data class BlockDetailState (
    val isLoading: Boolean = false,
    val block: AppUiModel? = null,
    val remainingTime: String = "00:00:00",
    val countdownMode: BlockCountdownMode =
        BlockCountdownMode.NONE,
    val isUnlocked: Boolean = false,
    val error: String? = null
)

enum class BlockCountdownMode {
    NONE,
    TEMPORARY_UNLOCK,
    REACTIVATION
}