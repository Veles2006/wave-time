package com.sae.wavetime.ui.block.detail

import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.ui.model.AppUiModel

data class BlockDetailState (
    val isLoading: Boolean = false,
    val block: AppUiModel? = null,
    val error: String? = null
)