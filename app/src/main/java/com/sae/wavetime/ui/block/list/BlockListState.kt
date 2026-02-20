package com.sae.wavetime.ui.block.list

import com.sae.wavetime.ui.model.AppUiModel

data class BlockListState(
    val isLoading: Boolean = false,
    val apps: List<AppUiModel> = emptyList(),
    val error: String? = null,
)