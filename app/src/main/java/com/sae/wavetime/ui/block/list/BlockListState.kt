package com.sae.wavetime.ui.block.list

import com.sae.wavetime.data.model.api.Block

data class BlockListState(
    val isLoading: Boolean = false,
    val apps: List<Block> = emptyList(),
    val error: String? = null,
)