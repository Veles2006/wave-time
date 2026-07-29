package com.sae.wavetime.ui.block.list

import com.sae.wavetime.ui.model.AppUiModel

data class BlockListState(
    val isLoading: Boolean = true,
    val blocks: List<AppUiModel> = emptyList(),
    val error: String? = null,
)